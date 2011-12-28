package org.vaadin.cytographer;

import giny.model.Edge;
import giny.model.Node;

import java.awt.Color;

import com.vaadin.terminal.PaintException;
import com.vaadin.terminal.PaintTarget;

import cytoscape.CyNode;
import cytoscape.Cytoscape;
import cytoscape.visual.EdgeAppearance;
import cytoscape.visual.NodeAppearance;
import cytoscape.visual.VisualMappingManager;
import cytoscape.visual.VisualPropertyType;
import cytoscape.visual.VisualStyle;

public class PaintController {

	public void repaintGraph(final PaintTarget target, final GraphProperties gp) throws PaintException {
		target.addAttribute("title", gp.getTitle());
		target.addAttribute("gwidth", gp.getWidth());
		target.addAttribute("gheight", gp.getHeight());
		target.addAttribute("texts", gp.isTextsVisible());

		final VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		final VisualStyle vs = vizmapper.getVisualStyle();

		final Color ec = (Color) vs.getEdgeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.EDGE_COLOR);
		final Color nbc = (Color) vs.getNodeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.NODE_BORDER_COLOR);
		final Color nfc = (Color) vs.getNodeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.NODE_FILL_COLOR);
		final Color nlc = (Color) vs.getNodeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.NODE_LABEL_COLOR);
		final Color elc = (Color) vs.getNodeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.EDGE_LABEL_COLOR);
		final Number elw = (Number) vs.getEdgeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.EDGE_LINE_WIDTH);
		final Number nbw = (Number) vs.getNodeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.NODE_LINE_WIDTH);
		final Number ns = (Number) vs.getNodeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.NODE_SIZE);
		final Number efs = (Number) vs.getNodeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.EDGE_FONT_SIZE);
		final Number nfs = (Number) vs.getNodeAppearanceCalculator().getDefaultAppearance().get(VisualPropertyType.NODE_FONT_SIZE);

		final Color bc = vs.getGlobalAppearanceCalculator().getDefaultBackgroundColor();
		final Color nsc = vs.getGlobalAppearanceCalculator().getDefaultNodeSelectionColor();
		final Color esc = vs.getGlobalAppearanceCalculator().getDefaultEdgeSelectionColor();

		target.addAttribute("bc", getRGB(bc));
		target.addAttribute("ec", getRGB(ec));
		target.addAttribute("elw", elw.intValue());
		target.addAttribute("nbc", getRGB(nbc));
		target.addAttribute("nfc", getRGB(nfc));
		target.addAttribute("nsc", getRGB(nsc));
		target.addAttribute("esc", getRGB(esc));
		target.addAttribute("nlc", getRGB(nlc));
		target.addAttribute("elc", getRGB(elc));
		target.addAttribute("nbw", nbw.intValue());
		target.addAttribute("ns", ns.intValue());
		target.addAttribute("efs", efs.intValue());
		target.addAttribute("nfs", nfs.intValue());

		for (final int ei : gp.getEdges()) {
			final Edge e = gp.getNetwork().getEdge(ei);
			final Node node1 = e.getSource();
			final Node node2 = e.getTarget();
			target.startTag("e");
			target.addAttribute("name", e.getIdentifier());
			target.addAttribute("node1", node1.getIdentifier());
			target.addAttribute("node2", node2.getIdentifier());

			final double xx1 = gp.getFinalView().getNodeView(node1).getXPosition();
			final double yy1 = gp.getFinalView().getNodeView(node1).getYPosition();
			final double xx2 = gp.getFinalView().getNodeView(node2).getXPosition();
			final double yy2 = gp.getFinalView().getNodeView(node2).getYPosition();

			int x1 = (int) xx1;
			int y1 = (int) yy1;
			int x2 = (int) xx2;
			int y2 = (int) yy2;

			if (gp.isUseFitting()) {
				x1 = (int) ((xx1 - gp.getMinX()) / gp.getCytoscapeViewWidth() * gp.getWidth());
				y1 = (int) ((yy1 - gp.getMinY()) / gp.getCytoscapeViewHeight() * gp.getHeight());
				x2 = (int) ((xx2 - gp.getMinX()) / gp.getCytoscapeViewWidth() * gp.getWidth());
				y2 = (int) ((yy2 - gp.getMinY()) / gp.getCytoscapeViewHeight() * gp.getHeight());
			}

			target.addAttribute("node1x", x1);
			target.addAttribute("node1y", y1);
			target.addAttribute("node2x", x2);
			target.addAttribute("node2y", y2);

			if (!gp.isStyleOptimization()) {
				final EdgeAppearance ea = vs.getEdgeAppearanceCalculator().calculateEdgeAppearance(e, gp.getNetwork());
				final NodeAppearance n1a = vs.getNodeAppearanceCalculator().calculateNodeAppearance(node1, gp.getNetwork());
				final NodeAppearance n2a = vs.getNodeAppearanceCalculator().calculateNodeAppearance(node2, gp.getNetwork());

				target.addAttribute("_ec", getRGB((Color) ea.get(VisualPropertyType.EDGE_COLOR)));
				target.addAttribute("_elw", ((Number) ea.get(VisualPropertyType.EDGE_LINE_WIDTH)).intValue());

				target.addAttribute("_n1bc", getRGB((Color) n1a.get(VisualPropertyType.NODE_BORDER_COLOR)));
				target.addAttribute("_n1fc", getRGB((Color) n1a.get(VisualPropertyType.NODE_FILL_COLOR)));
				target.addAttribute("_n1bw", ((Number) n1a.get(VisualPropertyType.NODE_LINE_WIDTH)).intValue());
				target.addAttribute("_n1s", ((Number) n1a.get(VisualPropertyType.NODE_SIZE)).intValue());

				target.addAttribute("_n2bc", getRGB((Color) n2a.get(VisualPropertyType.NODE_BORDER_COLOR)));
				target.addAttribute("_n2fc", getRGB((Color) n2a.get(VisualPropertyType.NODE_FILL_COLOR)));
				target.addAttribute("_n2bw", ((Number) n2a.get(VisualPropertyType.NODE_LINE_WIDTH)).intValue());
				target.addAttribute("_n2s", ((Number) n2a.get(VisualPropertyType.NODE_SIZE)).intValue());
			}

			target.endTag("e");
		}
	}

	public void paintNodeSize(final PaintTarget target, final GraphProperties graphProperties) throws PaintException {
		target.addAttribute("ns", (int) graphProperties.getNodeSize());
	}

	public void paintVisualStyle(final PaintTarget target, final GraphProperties graphProperties) throws PaintException {
		// TODO
	}

	public void paintTextVisibility(final PaintTarget target, final GraphProperties graphProperties) throws PaintException {
		target.addAttribute("texts", graphProperties.isTextsVisible());
	}

	public void paintOptimizedStyles(final PaintTarget target, final GraphProperties graphProperties) throws PaintException {
		// TODO
	}

	public void updateNode(final PaintTarget target, final GraphProperties graphProperties, final String nodeId) throws PaintException {
		final VisualMappingManager vizmapper = Cytoscape.getVisualMappingManager();
		final VisualStyle vs = vizmapper.getVisualStyle();
		final CyNode node = Cytoscape.getCyNode(nodeId);
		final NodeAppearance n1a = vs.getNodeAppearanceCalculator().calculateNodeAppearance(node, graphProperties.getNetwork());

		target.addAttribute("node", nodeId);
		target.addAttribute("_n1bc", getRGB((Color) n1a.get(VisualPropertyType.NODE_BORDER_COLOR)));
		target.addAttribute("_n1fc", getRGB((Color) n1a.get(VisualPropertyType.NODE_FILL_COLOR)));
		target.addAttribute("_n1bw", ((Number) n1a.get(VisualPropertyType.NODE_LINE_WIDTH)).intValue());
		target.addAttribute("_n1s", ((Number) n1a.get(VisualPropertyType.NODE_SIZE)).intValue());
	}

	private String getRGB(final Color bc) {
		return "rgb(" + bc.getRed() + "," + bc.getGreen() + "," + bc.getBlue() + ")";
	}

}