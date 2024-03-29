/* 
 * Copyright 2011 Johannes Tuikkala <johannes@vaadin.com>
 *                           LICENCED UNDER
 *                  GNU LESSER GENERAL PUBLIC LICENSE
 *                     Version 3, 29 June 2007
 */
package org.vaadin.cytographer.client.ui;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.vaadin.gwtgraphics.client.Group;
import org.vaadin.gwtgraphics.client.Shape;
import org.vaadin.gwtgraphics.client.VectorObject;
import org.vaadin.gwtgraphics.client.shape.Circle;
import org.vaadin.gwtgraphics.client.shape.Path;
import org.vaadin.gwtgraphics.client.shape.Rectangle;
import org.vaadin.gwtgraphics.client.shape.Text;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.Command;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VConsole;

public class VNode extends Group implements ContextListener, MouseDownHandler, MouseUpHandler, MouseMoveHandler, ClickHandler {

	private final VGraph graph;
	private float x;
	private float y;
	private Shape view;
	private final Text text;
	private final String name;
	private boolean textsVisible = true;
	private String originalFillColor;
	private final VCytographer cytographer;
	private Map<String, Command> commandMap;

	public VNode(final VCytographer cytographer, final VGraph graph, final Shape view, final String name) {
		super();
		this.cytographer = cytographer;
		this.graph = graph;
		this.view = view;
		this.name = name;
		text = new Text(view.getX() - view.getOffsetWidth() + 1, view.getY() + view.getOffsetHeight() / 2, name);
		text.setStrokeOpacity(0);
		add(view);
		add(text);

		addClickHandler(this);
		addMouseDownHandler(this);
		addMouseUpHandler(this);
		addMouseMoveHandler(this);
	}

	public void refreshNodeData(final UIDL child, final VVisualStyle style) {
		view.setFillColor(style.getNodeFillColor());
		view.setStrokeColor(style.getNodeBorderColor());
		view.setStrokeWidth(style.getNodeBorderWidth());

		setLabelColor(style.getNodeLabelColor());
		setFontSize(style.getNodeFontSize());
		setFontFamily(style.getFontFamily());
		setTextVisible(style.isTextsVisible());

		// node specific styles
		if (child.hasAttribute("_n1bc")) {
			view.setStrokeColor(child.getStringAttribute("_n1bc"));
		}
		if (child.hasAttribute("_n1fc")) {
			view.setFillColor(child.getStringAttribute("_n1fc"));
			setOriginalFillColor(view.getFillColor());
		}
		if (child.hasAttribute("_n1bw")) {
			view.setStrokeWidth(child.getIntAttribute("_n1bw"));
		}
		if (child.hasAttribute("_n1s")) {
			((Circle) view).setRadius(child.getIntAttribute("_n1s") / 2);
		}
	}

	public static VNode createANode(final UIDL child, final VCytographer cytographer, final VGraph graph, final String nodeName,
			final boolean firstNode, final VVisualStyle style) {

		Shape shape = null;
		if (firstNode) {
			shape = createShape(style, child.getIntAttribute("node1x"), child.getIntAttribute("node1y"));
		} else {
			shape = new Circle(child.getIntAttribute("node2x"), child.getIntAttribute("node2y"), style.getNodeSize());
		}
		shape.setFillColor(style.getNodeFillColor());
		shape.setStrokeColor(style.getNodeBorderColor());
		shape.setStrokeWidth(style.getNodeBorderWidth());

		final VNode node = new VNode(cytographer, graph, shape, nodeName);

		node.setLabelColor(style.getNodeLabelColor());
		node.setFontSize(style.getNodeFontSize());
		node.setFontFamily(style.getFontFamily());
		node.setTextVisible(style.isTextsVisible());
		node.setX(shape.getX());
		node.setY(shape.getY());

		// node specific styles
		if (child.hasAttribute("_n1bc")) {
			shape.setStrokeColor(child.getStringAttribute("_n1bc"));
		}
		if (child.hasAttribute("_n1fc")) {
			shape.setFillColor(child.getStringAttribute("_n1fc"));
			node.setOriginalFillColor(shape.getFillColor());
		}
		if (child.hasAttribute("_n1bw")) {
			shape.setStrokeWidth(child.getIntAttribute("_n1bw"));
		}
		if (child.hasAttribute("_n1s")) {
			if (shape instanceof Circle) {
				((Circle) shape).setRadius(child.getIntAttribute("_n1s") / 2);
			} else {

			}
		}
		return node;
	}

	private static Shape createShape(final VVisualStyle style, final int x, final int y) {
		Shape shape = null;
		final int size = style.getNodeSize();
		if (style.getNodeShape().equals("Rectangle")) {
			shape = new Rectangle(x, y, size, size);
		} else if (style.getNodeShape().equals("Triangle")) {
			shape = new Path(x, y);
			final float height = size * (float) Math.sqrt(3f) / 4f;
			((Path) shape).moveTo(x + size / 2, (int) (y + height));
			((Path) shape).moveTo(x + size, y);
			((Path) shape).moveTo(x, y);
		} else if (style.getNodeShape().equals("Diamond")) {
			shape = new Path(x, y);
			final float height = size * (float) Math.sqrt(3f) / 4f;
			((Path) shape).moveTo(x + size / 2, (int) (y + height));
			((Path) shape).moveTo(x + size, y);
			((Path) shape).moveTo(x + size / 2, (int) (y - height));
			((Path) shape).moveTo(x, y);
		} else {
			shape = new Circle(x, y, size);
		}
		return shape;
	}

	public static VNode createANode(final float x, final float y, final VCytographer cytographer, final VGraph graph,
			final VVisualStyle style) {
		final Circle shape = new Circle((int) x, (int) y, style.getNodeSize());

		shape.setFillColor(style.getNodeFillColor());
		shape.setStrokeColor(style.getNodeBorderColor());
		shape.setStrokeWidth(style.getNodeBorderWidth());

		final VNode node = new VNode(cytographer, graph, shape, "tmp" + new Random().nextInt(1000000));
		node.setX(x);
		node.setY(y);
		node.setLabelColor(style.getNodeLabelColor());
		node.setFontSize(style.getNodeFontSize());
		node.setFontFamily(style.getFontFamily());
		node.setTextVisible(style.isTextsVisible());
		node.setOriginalFillColor(style.getNodeFillColor());

		return node;
	}

	@Override
	protected Class<? extends VectorObject> getType() {
		return Group.class;
	}

	public void setView(final Shape view) {
		this.view = view;
	}

	public Shape getView() {
		return view;
	}

	public String getName() {
		return name;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setFillColor(final String color) {
		view.setFillColor(color);
	}

	public void setLabelColor(final String color) {
		text.setFillColor(color);
		text.setFillOpacity(1);
	}

	public void setX(final float x) {
		this.x = x;
		view.setX((int) x);
		text.setX((int) x);
	}

	public void setY(final float y) {
		this.y = y;
		view.setY((int) y);
		text.setY((int) y);
	}

	public void setFontSize(final int nodeFontSize) {
		text.setFontSize(nodeFontSize);
	}

	public void setFontFamily(final String family) {
		text.setFontFamily(family);
	}

	public void setTextVisible(final boolean visible) {
		if (!visible && textsVisible) {
			remove(text);
		} else if (visible && !textsVisible) {
			add(text);
		}
		textsVisible = visible;
	}

	public String getOriginalFillColor() {
		return originalFillColor;
	}

	public void setOriginalFillColor(final String originalFillColor) {
		this.originalFillColor = originalFillColor;
	}

	@Override
	public String toString() {
		return name;
	}

	public void setRadius(final int nodeSize) {
		try {
			((Circle) view).setRadius(nodeSize);
		} catch (final Exception e) {
			view.setPixelSize(nodeSize, nodeSize);
		}
	}

	public void moveNode(final float x, final float y) {
		setX(x);
		setY(y);
		graph.updateEdges(this, true);
	}

	@Override
	public void onClick(final ClickEvent event) {
		if (cytographer.isOnLink()) {
			cytographer.constructLinkTo(this);
		} else {
			graph.setNodeSelected((VNode) event.getSource(), !graph.getSelectedShapes().contains(event.getSource()));
			cytographer.nodeOrEdgeSelectionChanged();
		}
	}

	@Override
	public void onMouseMove(final MouseMoveEvent event) {
	}

	@Override
	public void onMouseUp(final MouseUpEvent event) {
		graph.setMovedShape(null);
	}

	@Override
	public void onMouseDown(final MouseDownEvent event) {
		if (event.getNativeEvent().getButton() == NativeEvent.BUTTON_RIGHT) {
			VConsole.log("rightClick");
			final VContextMenu menu = new VContextMenu(VNode.this);
			menu.showMenu(event.getClientX(), event.getClientY());
			cytographer.setCurrentMenu(menu);
		} else {
			graph.setMovedShape(this);
		}
		event.stopPropagation();
	}

	@Override
	public void initCommands(final VContextMenu menu) {
		commandMap = new HashMap<String, Command>();
		final Command editCommand = menu.new ContextMenuCommand() {
			@Override
			public void execute() {
				super.execute();
				cytographer.editNode(VNode.this);
			}
		};
		final Command linkCommand = menu.new ContextMenuCommand() {
			@Override
			public void execute() {
				super.execute();
				cytographer.startLinkingFrom(VNode.this);
			}
		};
		final Command delCommand = menu.new ContextMenuCommand() {
			@Override
			public void execute() {
				super.execute();
				cytographer.deleteNode(VNode.this, true);
			}
		};
		// commandMap.put("Edit", editCommand);
		commandMap.put("Link to", linkCommand);
		commandMap.put("Delete", delCommand);
	}

	@Override
	public Command[] getCommands() {
		return commandMap.values().toArray(new Command[2]);
	}

	@Override
	public String getCommandName(final Command command) {
		for (final Map.Entry<String, Command> entry : commandMap.entrySet()) {
			if (entry.getValue().equals(command)) {
				return entry.getKey();
			}
		}
		return null;
	}
}
