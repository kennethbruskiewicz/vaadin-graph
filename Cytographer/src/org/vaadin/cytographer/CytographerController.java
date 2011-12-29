package org.vaadin.cytographer;

import java.util.Random;

import cytoscape.CyNetwork;
import cytoscape.Cytoscape;
import cytoscape.CytoscapeInit;
import cytoscape.data.readers.CytoscapeSessionReader;
import cytoscape.layout.CyLayoutAlgorithm;
import cytoscape.view.CyNetworkView;

public class CytographerController {

	static {
		final CytoscapeInit init = new CytoscapeInit();
		init.init(new CytographerInit());
	}

	private String currentFileName;
	private Cytographer currentGraph;
	private CyNetworkView currentView;
	private final CytographerApplication app;

	private final int width;
	private final int height;

	public CytographerController(final CytographerApplication app, final int width, final int height) {
		this.app = app;
		this.width = width;
		this.height = height;
	}

	public void loadCytoscapeSession(final String fileName) {
		final CyNetwork net = Cytoscape.createNetwork(fileName, false);
		currentView = Cytoscape.createNetworkView(net);
		currentFileName = fileName;

		Cytoscape.createNewSession();
		Cytoscape.setSessionState(Cytoscape.SESSION_OPENED);
		Cytoscape.createNewSession();
		Cytoscape.setSessionState(Cytoscape.SESSION_NEW);
		CytoscapeSessionReader sr;
		try {
			sr = new CytoscapeSessionReader(fileName, null);
			sr.read();
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			sr = null;
		}
		Cytoscape.setCurrentSessionFileName(fileName);

		final Cytographer graph = new Cytographer(net, currentView, fileName, width, height);
		graph.setImmediate(true);
		graph.setWidth(width + "px");
		graph.setHeight(height + "px");
		currentGraph = graph;
		currentGraph.setNodeSize(10, false);
		app.paintGraph(currentGraph);
	}

	public void loadNetworkGraph(final String fileName) {
		Cytoscape.createNewSession();
		final CyNetwork net = Cytoscape.createNetworkFromFile(fileName);
		currentView = Cytoscape.createNetworkView(net);
		currentFileName = fileName;

		final Cytographer graph = new Cytographer(net, currentView, fileName, width, height);
		graph.setImmediate(true);
		graph.setWidth(width + "px");
		graph.setHeight(height + "px");
		currentGraph = graph;
		currentGraph.setNodeSize(10, false);
		app.paintGraph(currentGraph);
	}

	public void createNewNetworkGraph() {
		Cytoscape.createNewSession();
		final String name = "New network " + new Random().nextInt(100);
		final CyNetwork net = Cytoscape.createNetwork(name, false);
		currentView = Cytoscape.createNetworkView(net);

		final Cytographer graph = new Cytographer(net, currentView, name, width, height);
		graph.setImmediate(true);
		graph.setWidth(width + "px");
		graph.setHeight(height + "px");
		currentGraph = graph;
		currentGraph.setNodeSize(10, false);
		app.paintGraph(currentGraph);
	}

	public void applyLayoutAlgorithm(final CyLayoutAlgorithm loAlgorithm) {
		currentView.applyLayout(loAlgorithm);
		currentGraph.repaintGraph();
	}

	public Cytographer getCurrentGraph() {
		return currentGraph;
	}

	public void repaintGraph() {
		app.paintGraph(currentGraph);
	}

}