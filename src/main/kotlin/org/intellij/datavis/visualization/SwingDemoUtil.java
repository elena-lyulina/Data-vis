package org.intellij.datavis.visualization;

import com.intellij.ui.JBColor;
import com.intellij.util.ui.UIUtil;
import jetbrains.datalore.base.geometry.DoubleVector;
import jetbrains.datalore.base.observable.property.Property;
import jetbrains.datalore.base.observable.property.ReadableProperty;
import jetbrains.datalore.base.observable.property.ValueProperty;
import jetbrains.datalore.visualization.gog.DemoAndTest;
import jetbrains.datalore.visualization.gog.plot.Plot;
import jetbrains.datalore.visualization.gog.plot.PlotContainer;
import jetbrains.datalore.visualization.svg.SvgSvgElement;
import jetbrains.datalore.visualization.svgToAwt.SvgAwtComponent;
import jetbrains.datalore.visualization.svgToAwt.SvgAwtHelper.MessageCallback;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Map;
import java.io.File;


/**
 * Demo class for drawing charts using ggplot lib
 */
public final class SwingDemoUtil {

    public static void show(DoubleVector viewSize, Map<String, Object> plotSpec, JPanel panel) {

        JComponent component = createComponent(viewSize, plotSpec);

        component.setBorder(BorderFactory.createLineBorder(JBColor.ORANGE, 1));

        component.setMinimumSize(new Dimension((int) viewSize.x, (int) viewSize.y));
        component.setMaximumSize(new Dimension((int) viewSize.x, (int) viewSize.y));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);

        panel.add(Box.createRigidArea(new Dimension(0, 5)));
        panel.add(component);

    }


    public static BufferedImage getImageFromPlotSpec(Map<String, Object> plotSpec, int width, int height) {
        DoubleVector viewSize = new DoubleVector(width, height);
        ComponentWrapper wrapper = createComponent(viewSize, plotSpec);

        BufferedImage bufferedImage = UIUtil.createImage((int) viewSize.x,
                (int) viewSize.y, BufferedImage.TYPE_INT_ARGB);

       // BufferedImage bufferedImage = new BufferedImage((int) viewSize.x, (int) viewSize.y, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D) bufferedImage.getGraphics();

        g2d.setPaint (JBColor.WHITE);
        g2d.fillRect ( 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight() );


        wrapper.paintComponentWrapper(g2d);
        g2d.dispose();
        return bufferedImage;
    }


    private static class MouseListener extends MouseAdapter {
        int i = 0;
        Plot plot;

        public MouseListener(Plot plot) {
            this.plot = plot;
        }
        public void mouseClicked(MouseEvent e) {
            System.out.println("pressed " + i);
            i++;
            ReadableProperty<DoubleVector> size = plot.laidOutSize();
            System.out.println(plot.laidOutSize().getPropExpr());
//            System.out.println(plot.preferredSize().getPropExpr());

        }
    }



    static class ComponentWrapper extends SvgAwtComponent {

        ComponentWrapper(SvgSvgElement svgRoot) {
            super(svgRoot);
        }

        void paintComponentWrapper(Graphics g) {
            super.paintComponent(g);
        }

        @Override
        protected MessageCallback createMessageCallback() {
            return createDefaultMessageCallback();
        }
    }

    private static ComponentWrapper createComponent(DoubleVector viewSize, Map<String, Object> plotSpec) {
        Plot plot = DemoAndTest.createPlot(plotSpec, false);
        PlotContainer plotContainer = new PlotContainer(plot, new ValueProperty<>(viewSize));

        plotContainer.ensureContentBuilt();

        SvgSvgElement svgRoot = plotContainer.getSvg();

        ComponentWrapper wrapper = new ComponentWrapper(svgRoot);

//        SvgAwtComponent component = new SvgAwtComponent(svgRoot) {
//            @Override
//            protected MessageCallback createMessageCallback() {
//                return createDefaultMessageCallback();
//            }
//        };


        wrapper.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                plotContainer.mouseLeft();
            }
        });

        return wrapper;
    }



    private SwingDemoUtil() {
    }
}
