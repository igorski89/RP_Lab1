import info.clearthought.layout.TableLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.DecimalFormat;

/**
 * Created by IntelliJ IDEA.
 * User: igorevsukov
 * Date: Oct 27, 2009
 * Time: 1:27:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends JFrame {

    private JMenuBar menuBar = new JMenuBar();
    private JMenu fileMenu = new JMenu("File");
    private JMenuItem openFileMenuItem = new JMenuItem("Open");
    private JMenuItem exitMenuItem = new JMenuItem("Exit");

    private JTabbedPane tabs = new JTabbedPane();

    private Sample sample = new Sample();
    private JTable sampleTable;
    private AbstractTableModel sampleTableModel;
    private XYSeriesCollection sampleGraphDataset;

    private JTable autocorelationTable;
    private AbstractTableModel autocorelationTableModel;
    private XYSeriesCollection autocorelationGraphDataset;

    private JTextArea trendTextArea = new JTextArea();

    private DecimalFormat doubleFormateer = new DecimalFormat("#.###");

    private JTable medianSmoothTable;
    private AbstractTableModel medianSmoothTableModel;
    private XYSeriesCollection medianSmoothGraphDataset;

    private JTable lsSmoothTable;
    private AbstractTableModel lsSmoothTableModel;
    private XYSeriesCollection lsSmoothGraphDataset;

    private JTable s31Table;
    private AbstractTableModel s31TableModel;
    private XYSeriesCollection s31GraphDataset;

    private JTable s42Table;
    private AbstractTableModel s42TableModel;
    private XYSeriesCollection s42GraphDataset;

    public MainFrame() {
        setTitle("RP - Lab 1 - Main Window");

        //menu
        openFileMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser openFileChooser = new JFileChooser();
                openFileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                openFileChooser.setMultiSelectionEnabled(false);
                // TODO: убрать в релизе
				openFileChooser.setCurrentDirectory(new File("/Users/igorevsukov/Documents/DNU/RP/RP_Lab_1/"));
                if (openFileChooser.showOpenDialog(getParent()) == JFileChooser.APPROVE_OPTION) {
                    String fileName = openFileChooser.getSelectedFile().getAbsolutePath();
                    sample = new Sample(fileName);
                    sampleTable.tableChanged(null);
                    autocorelationTable.tableChanged(null);
                    medianSmoothTable.tableChanged(null);
                    lsSmoothTable.tableChanged(null);
                    s31Table.tableChanged(null);
                    s42Table.tableChanged(null);
                    refreshSampleGraphDataset();
                    refreshAutocorelationDataset();
                    refreshMedianSmoothDataset();
                    refreshLsSmoothDataset();
                    refreshS31Dataset();
                    refreshTrendTextArea();
                    refreshS42Dataset();
                }
            }

        });

        fileMenu.add(openFileMenuItem);
        fileMenu.addSeparator();

        exitMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        fileMenu.add(exitMenuItem);
        menuBar.add(fileMenu);

        setJMenuBar(menuBar);

        //original table
		sampleTableModel = new AbstractTableModel(){
			private static final long serialVersionUID = 1L;

			@Override
			public int getColumnCount() {
                return 3;
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                else return Double.class;
			}

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "i";
                    case 1: return "original";
                    case 2: return "processed";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() {
                return sample == null ? 0 : sample.size();
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return rowIndex;
                    case 1: return sample.getOriginalSample().get(rowIndex);
                    case 2: return sample.get(rowIndex);
                    default: return Double.NaN;   
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
		sampleTable = new JTable(sampleTableModel);

        sampleGraphDataset = new XYSeriesCollection();
        JFreeChart sampleChart = ChartFactory.createScatterPlot("Sample","i","x[i]",sampleGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        XYPlot plot = sampleChart.getXYPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);        
        renderer.setSeriesLinesVisible(1, true);
        renderer.setSeriesShapesVisible(1, true);
        plot.setRenderer(renderer);
        
        ChartPanel sampleChartPanel = new ChartPanel(sampleChart);
        sampleChartPanel.setVerticalAxisTrace(true);
        sampleChartPanel.setHorizontalAxisTrace(true);
        sampleChartPanel.setDoubleBuffered(true);

        JPanel samplePanel = new JPanel(new TableLayout(new double[][] {{0.30,0.70},{TableLayout.FILL}}));
		samplePanel.add(new JScrollPane(sampleTable),"0, 0");
		samplePanel.add(sampleChartPanel,"1, 0");
		tabs.add(samplePanel, "Sample",0);

        autocorelationTableModel = new AbstractTableModel(){
			@Override
			public int getColumnCount() {
                return sample.getAutocorelationCoficients() == null ? 0 : sample.getAutocorelationCoficients().length + 1;
//                return sample.getAutocorelationCoficients() == null ? 0 : sample.getAutocorelationCoficients().length;
			}

//			@Override
//			public Class<?> getColumnClass(int columnIndex) {
//                if (columnIndex == 0) return String.class;
//                else return Double.class;
//			}

			@Override
			public String getColumnName(int columnIndex) {
                if (columnIndex == 0) return "k \\ tau";
                return String.valueOf(columnIndex);
			}

			@Override
			public int getRowCount() {
                return sample.getAutocorelationCoficients() == null ? 0 : sample.getAutocorelationCoficients()[0].length+3;
//                return sample.getAutocorelationCoficients() == null ? 0 : sample.getAutocorelationCoficients()[0].length;
			}

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
                if (columnIndex == 0) {
                    if (rowIndex < sample.getAutocorelationCoficients()[0].length) {
                        return String.valueOf(rowIndex + 1);
                    }
                    else if (rowIndex == sample.getAutocorelationCoficients()[0].length) {
                        return "Statistic";
                    }
                    else if (rowIndex == sample.getAutocorelationCoficients()[0].length + 1) {
                        return "Quantile";
                    }
                    else if (rowIndex == sample.getAutocorelationCoficients()[0].length + 2) {
                        return "Result";
                    }
                    else {
                        return "WTF";
                    }
                }
                else if (rowIndex == sample.getAutocorelationCoficients()[0].length) {
                    columnIndex--;
//                    return sample.getAutocorelationStatistics()[columnIndex];
                    return Double.valueOf(doubleFormateer.format(sample.getAutocorelationStatistics()[columnIndex]));
                }
                else if (rowIndex == sample.getAutocorelationCoficients()[0].length+1) {
                    columnIndex--;
//                    return sample.getAutocorelationQuantiles()[columnIndex];
                    return Double.valueOf(doubleFormateer.format(sample.getAutocorelationQuantiles()[columnIndex]));
                }
                else if (rowIndex == sample.getAutocorelationCoficients()[0].length+2) {
                    columnIndex--;
                    return sample.getAutocorelationSimilarities()[columnIndex];
                }
                else {
                    columnIndex--;
//                    return sample.getAutocorelationCoficients()[rowIndex][columnIndex];
                    return Double.valueOf(doubleFormateer.format(sample.getAutocorelationCoficients()[columnIndex][rowIndex]));
                }
//                return Double.valueOf(doubleFormateer.format(sample.getAutocorelationCoficients()[columnIndex][rowIndex]));
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        autocorelationTable = new JTable(autocorelationTableModel);
        autocorelationTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        autocorelationGraphDataset = new XYSeriesCollection();
        JFreeChart autocorelationChart = ChartFactory.createScatterPlot("Autocorelation","","",autocorelationGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        XYPlot autocorelationPlot = autocorelationChart.getXYPlot();
        XYLineAndShapeRenderer autocorelationRenderer = new XYLineAndShapeRenderer();
        autocorelationRenderer.setSeriesLinesVisible(0, true);
        autocorelationRenderer.setSeriesShapesVisible(0, true);
        autocorelationRenderer.setSeriesLinesVisible(1, true);
        autocorelationRenderer.setSeriesShapesVisible(1, false);
        autocorelationPlot.setRenderer(autocorelationRenderer);

        ChartPanel autocorelationChartPanel = new ChartPanel(autocorelationChart);
        autocorelationChartPanel.setVerticalAxisTrace(true);
        autocorelationChartPanel.setHorizontalAxisTrace(true);
        autocorelationChartPanel.setDoubleBuffered(true);

        JPanel autocorelationPanel = new JPanel(new TableLayout(new double[][] {{TableLayout.FILL},{0.50,0.50}}));
        autocorelationPanel.add(new JScrollPane(autocorelationTable),"0, 0");
        autocorelationPanel.add(autocorelationChartPanel, "0, 1");
        tabs.add("Autocorelation",autocorelationPanel);

        tabs.add("Trend",new JScrollPane(trendTextArea));

        medianSmoothTableModel = new AbstractTableModel(){
			@Override
			public int getColumnCount() { return 2; }

			@Override
			public Class<?> getColumnClass(int columnIndex) {
                return Double.class;
			}

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "t";
                    case 1: return "x[t]";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null ? 0 : sample.getMedianSmoothedSample().size(); }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return sample.getMedianSmoothedSample().get(rowIndex).getX();
                    case 1: return sample.getMedianSmoothedSample().get(rowIndex).getY();
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        medianSmoothTable = new JTable(medianSmoothTableModel);

        medianSmoothGraphDataset = new XYSeriesCollection();
        JFreeChart medianSmoothChart = ChartFactory.createScatterPlot("Median Smooth","t","x(t)",medianSmoothGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        XYPlot medianSmoothPlot = medianSmoothChart.getXYPlot();
        XYLineAndShapeRenderer medianSmoothRenderer = new XYLineAndShapeRenderer();
        medianSmoothRenderer.setSeriesLinesVisible(0, true);
        medianSmoothRenderer.setSeriesShapesVisible(0, true);
        medianSmoothRenderer.setSeriesLinesVisible(1, true);
        medianSmoothRenderer.setSeriesShapesVisible(1, false);
        medianSmoothPlot.setRenderer(medianSmoothRenderer);

        ChartPanel medianSmoothChartPanel = new ChartPanel(medianSmoothChart);
        medianSmoothChartPanel.setVerticalAxisTrace(true);
        medianSmoothChartPanel.setHorizontalAxisTrace(true);
        medianSmoothChartPanel.setDoubleBuffered(true);

        JPanel medianSmoothPanel = new JPanel(new TableLayout(new double[][] {{0.30,0.70},{TableLayout.FILL}}));
        medianSmoothPanel.add(new JScrollPane(medianSmoothTable),"0, 0");
        medianSmoothPanel.add(medianSmoothChartPanel, "1, 0");
        tabs.add("Median smooth", medianSmoothPanel);


        lsSmoothTableModel = new AbstractTableModel(){
			@Override
			public int getColumnCount() { return 2; }

			@Override
			public Class<?> getColumnClass(int columnIndex) { return Double.class; }

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "t";
                    case 1: return "x[t]";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null ? 0 : sample.getLsSmoothedSample().size(); }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return rowIndex;
                    case 1: return sample.getLsSmoothedSample().get(rowIndex);
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        lsSmoothTable = new JTable(lsSmoothTableModel);
        lsSmoothGraphDataset = new XYSeriesCollection();
        JFreeChart mnkSmoothChart = ChartFactory.createScatterPlot("Least squares Smooth","t","x(t)", lsSmoothGraphDataset, PlotOrientation.VERTICAL,true,true,false);
        XYPlot mnkSmoothPlot = mnkSmoothChart.getXYPlot();
        XYLineAndShapeRenderer mnkSmoothRenderer = new XYLineAndShapeRenderer();
        mnkSmoothRenderer.setSeriesLinesVisible(0, true);
        mnkSmoothRenderer.setSeriesShapesVisible(0, true);
        mnkSmoothRenderer.setSeriesLinesVisible(1, true);
        mnkSmoothRenderer.setSeriesShapesVisible(1, false);
        mnkSmoothPlot.setRenderer(mnkSmoothRenderer);

        ChartPanel mnkSmoothChartPanel = new ChartPanel(mnkSmoothChart);
        mnkSmoothChartPanel.setVerticalAxisTrace(true);
        mnkSmoothChartPanel.setHorizontalAxisTrace(true);
        mnkSmoothChartPanel.setDoubleBuffered(true);

        JPanel mnkSmoothPanel = new JPanel(new TableLayout(new double[][] {{0.30,0.70},{TableLayout.FILL}}));
        mnkSmoothPanel.add(new JScrollPane(lsSmoothTable),"0, 0");
        mnkSmoothPanel.add(mnkSmoothChartPanel, "1, 0");
        tabs.add("LS smooth", mnkSmoothPanel);

        s31TableModel = new AbstractTableModel(){
			@Override
			public int getColumnCount() { return 2; }

			@Override
			public Class<?> getColumnClass(int columnIndex) { return Double.class; }

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "t";
                    case 1: return "x(t)";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null ? 0 : sample.getS31().size(); }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return sample.getS31().get(rowIndex).getX();
                    case 1: return sample.getS31().get(rowIndex).getY();
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        s31Table = new JTable(s31TableModel);
        s31GraphDataset = new XYSeriesCollection();
        JFreeChart s31Chart = ChartFactory.createScatterPlot("s31","t","x(t)", s31GraphDataset, PlotOrientation.VERTICAL,true,true,false);
        XYPlot s31Plot = s31Chart.getXYPlot();
        XYLineAndShapeRenderer s31Renderer = new XYLineAndShapeRenderer();
        s31Renderer.setSeriesLinesVisible(0, true);
        s31Renderer.setSeriesShapesVisible(0, true);
        s31Renderer.setSeriesLinesVisible(1, true);
        s31Renderer.setSeriesShapesVisible(1, false);
        s31Plot.setRenderer(s31Renderer);

        ChartPanel s31ChartPanel = new ChartPanel(s31Chart);
        s31ChartPanel.setVerticalAxisTrace(true);
        s31ChartPanel.setHorizontalAxisTrace(true);
        s31ChartPanel.setDoubleBuffered(true);

        JPanel s31Panel = new JPanel(new TableLayout(new double[][] {{0.30,0.70},{TableLayout.FILL}}));
        s31Panel.add(new JScrollPane(s31Table),"0, 0");
        s31Panel.add(s31ChartPanel, "1, 0");
        tabs.add("S31", s31Panel);

        s42TableModel = new AbstractTableModel(){
			@Override
			public int getColumnCount() { return 2; }

			@Override
			public Class<?> getColumnClass(int columnIndex) { return Double.class; }

			@Override
			public String getColumnName(int columnIndex) {
                switch (columnIndex) {
                    case 0: return "t";
                    case 1: return "x(t)";
                    default: return "";
                }
			}

			@Override
			public int getRowCount() { return sample == null ? 0 : sample.getS42().size(); }

			@Override
			public Object getValueAt(int rowIndex, int columnIndex) {
				switch (columnIndex) {
                    case 0: return sample.getS42().get(rowIndex).getX();
                    case 1: return sample.getS42().get(rowIndex).getY();
                    default: return Double.NaN;
                }
			}

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {	return false; }
		};
        s42Table = new JTable(s42TableModel);
        s42GraphDataset = new XYSeriesCollection();
        JFreeChart s42Chart = ChartFactory.createScatterPlot("s42","t","x(t)", s42GraphDataset, PlotOrientation.VERTICAL,true,true,false);
        XYPlot s42Plot = s42Chart.getXYPlot();
        XYLineAndShapeRenderer s42Renderer = new XYLineAndShapeRenderer();
        s42Renderer.setSeriesLinesVisible(0, true);
        s42Renderer.setSeriesShapesVisible(0, true);
        s42Renderer.setSeriesLinesVisible(1, true);
        s42Renderer.setSeriesShapesVisible(1, false);
        s42Plot.setRenderer(s42Renderer);

        ChartPanel s42ChartPanel = new ChartPanel(s42Chart);
        s42ChartPanel.setVerticalAxisTrace(true);
        s42ChartPanel.setHorizontalAxisTrace(true);
        s42ChartPanel.setDoubleBuffered(true);

        JPanel s42Panel = new JPanel(new TableLayout(new double[][] {{0.30,0.70},{TableLayout.FILL}}));
        s42Panel.add(new JScrollPane(s42Table),"0, 0");
        s42Panel.add(s42ChartPanel, "1, 0");
        tabs.add("S42", s42Panel);



		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(tabs, BorderLayout.CENTER);
    }

    public void refreshSampleGraphDataset() {
		sampleGraphDataset.removeAllSeries();
		XYSeries originalSeries = new XYSeries("original");
		for (int i = 0; i < sample.getOriginalSample().size(); i++) {
			originalSeries.add(i, sample.getOriginalSample().get(i).doubleValue());
		}
		sampleGraphDataset.addSeries(originalSeries);

        XYSeries series = new XYSeries("without anomals");
		for (int i = 0; i < sample.size(); i++) {
			series.add(i, sample.get(i));
		}
		sampleGraphDataset.addSeries(series);

//        XYSeries medianSmoothed = new XYSeries("Median Smooth");
//        for (int i = 0; i < sample.getMedianSmoothedSample().size(); i++) {
//            medianSmoothed.add(sample.getMedianSmoothedSample().get(i).getX(),sample.getMedianSmoothedSample().get(i).getY());
//        }
//        sampleGraphDataset.addSeries(medianSmoothed);
	}

    public void refreshMedianSmoothDataset() {
        medianSmoothGraphDataset.removeAllSeries();

        XYSeries sampleData = new XYSeries("sample");
		for (int i = 0; i < sample.size(); i++) {
			sampleData.add(i, sample.get(i));
		}
		medianSmoothGraphDataset.addSeries(sampleData);

        XYSeries medianSmoothed = new XYSeries("Median Smooth");
        for (int i = 0; i < sample.getMedianSmoothedSample().size(); i++) {
            medianSmoothed.add(sample.getMedianSmoothedSample().get(i).getX(),sample.getMedianSmoothedSample().get(i).getY());
        }
        medianSmoothGraphDataset.addSeries(medianSmoothed);
    }

    public void refreshLsSmoothDataset() {
        lsSmoothGraphDataset.removeAllSeries();

        XYSeries sampleData = new XYSeries("sample");
		for (int i = 0; i < sample.size(); i++) {
			sampleData.add(i, sample.get(i));
		}
		lsSmoothGraphDataset.addSeries(sampleData);

        XYSeries mnkSmoothed = new XYSeries("LS Smooth");
        for (int i = 0; i < sample.getLsSmoothedSample().size(); i++) {
            mnkSmoothed.add((double)i,sample.getLsSmoothedSample().get(i));
        }
        lsSmoothGraphDataset.addSeries(mnkSmoothed);
    }

    public void refreshAutocorelationDataset() {
        autocorelationGraphDataset.removeAllSeries();

        XYSeries series = new XYSeries("autocorelation");
        for(int i=0; i<sample.getAutocorelationCoficients().length; i++) {
            series.add(i,sample.getAutocorelationCoficients()[i][0]);
        }
        autocorelationGraphDataset.addSeries(series);
    }

    public void refreshS31Dataset() {
        s31GraphDataset.removeAllSeries();

        XYSeries sampleData = new XYSeries("sample");
		for (int i = 0; i < sample.size(); i++) {
			sampleData.add(i, sample.get(i));
		}
		s31GraphDataset.addSeries(sampleData);

        XYSeries s31 = new XYSeries("S31");
        for (int i = 0; i < sample.getS31().size(); i++) {
            s31.add(sample.getS31().get(i).getX(),sample.getS31().get(i).getY());
        }
        s31GraphDataset.addSeries(s31);

    }

    public void refreshS42Dataset() {
        s42GraphDataset.removeAllSeries();

        XYSeries sampleData = new XYSeries("sample");
		for (int i = 0; i < sample.size(); i++) {
			sampleData.add(i, sample.get(i));
		}
		s42GraphDataset.addSeries(sampleData);

        XYSeries s42 = new XYSeries("S42");
        for (int i = 0; i < sample.getS42().size(); i++) {
            s42.add(sample.getS42().get(i).getX(), sample.getS42().get(i).getY());
        }
        s42GraphDataset.addSeries(s42);
    }


    public void refreshTrendTextArea() {
        trendTextArea.setText("");
        boolean isHasTrend = sample.isHaveTrend();
        final double alpha = 0.95;
        trendTextArea.append("Range criterium:\n");
        trendTextArea.append(String.format("p=%d\n",sample.getTrendP()));
        trendTextArea.append(String.format("ранговый коэфициент Кэндалла ро=%f\n",sample.getTrendRo()));        
        trendTextArea.append(String.format("stohastich haracteristics z=%f\n",sample.getTrendZ()));
        trendTextArea.append(String.format("Quantile u(%3.2f/2)=%f\n",alpha,Quantiles.norm(0.95/2)));
        trendTextArea.append("Has trend:"+String.valueOf(isHasTrend));
    }


}
