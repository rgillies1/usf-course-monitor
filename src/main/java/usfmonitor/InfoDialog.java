package usfmonitor;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import java.awt.FlowLayout;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.awt.event.ActionEvent;

public class InfoDialog extends JDialog {
		
	private final int DEFAULT_WIDTH = 400;
	private final int DEFAULT_HEIGHT = 400;
	
	private static final long serialVersionUID = 6475412557778087050L;
	private final JPanel botPanel = new JPanel();
	private final JCheckBox botCheckbox = new JCheckBox("Send notifications via Discord bot");
	private final JPanel tokenPanel = new JPanel();
	private final JLabel tokenLabel = new JLabel("Bot token:");
	private final JTextField tokenField = new JTextField();
	private final JPanel idPanel = new JPanel();
	private final JLabel idLabel = new JLabel("Discord ID:");
	private final JTextField idField = new JTextField();
	private final JPanel seleniumPanel = new JPanel();
	private final JCheckBox seleniumCheckbox = new JCheckBox("Attempt to automatically register via Chromium");
	private final JPanel installPathPanel = new JPanel();
	private final JLabel installPathLabel = new JLabel("Chromium install path:");
	private final JTextField installPathField = new JTextField();
	private final JPanel OASISpanel = new JPanel();
	private final JLabel OASISPathLabel = new JLabel("OASIS direct login path:");
	private final JTextField OASISPathField = new JTextField();
	private final JPanel CRNPanel = new JPanel();
	private final JLabel CRNLabel = new JLabel("Course CRNs (one per line):");
	private final JTextArea CRNArea = new JTextArea();
	private final JScrollPane scrollPane = new JScrollPane();
	private final JPanel buttonPanel = new JPanel();
	private final JButton quitButton = new JButton("Quit");
	private final JButton startButton = new JButton("Start");
	
	public InfoDialog(JFrame parent) {
		super(parent, "USF OASIS Course Monitor");
		OASISPathField.setColumns(20);
		installPathField.setColumns(20);
		idField.setColumns(20);
		tokenField.setColumns(20);
		this.setPreferredSize(new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT));
		getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		getContentPane().add(botPanel);
		botPanel.setLayout(new BoxLayout(botPanel, BoxLayout.Y_AXIS));
		botCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
		botPanel.add(botCheckbox);
		botPanel.add(tokenPanel);
		tokenPanel.add(tokenLabel);
		tokenPanel.add(tokenField);
		botPanel.add(idPanel);
		idPanel.add(idLabel);
		idPanel.add(idField);
		
		getContentPane().add(seleniumPanel);
		seleniumPanel.setLayout(new BoxLayout(seleniumPanel, BoxLayout.Y_AXIS));
		seleniumCheckbox.setAlignmentX(Component.CENTER_ALIGNMENT);
		seleniumPanel.add(seleniumCheckbox);
		seleniumPanel.add(installPathPanel);
		installPathPanel.add(installPathLabel);
		installPathPanel.add(installPathField);
		seleniumPanel.add(OASISpanel);
		OASISpanel.add(OASISPathLabel);
		OASISpanel.add(OASISPathField);
		
		getContentPane().add(CRNPanel);
		CRNPanel.setLayout(new BoxLayout(CRNPanel, BoxLayout.Y_AXIS));
		CRNLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		CRNPanel.add(CRNLabel);
		CRNPanel.add(scrollPane);
		CRNArea.setRows(7);
		CRNArea.setColumns(30);
		scrollPane.setViewportView(CRNArea);
		scrollPane.setMinimumSize(new Dimension(this.getPreferredSize().width, Short.MAX_VALUE));
		CRNPanel.setMinimumSize(new Dimension(this.getPreferredSize().width, Short.MAX_VALUE));
		CRNPanel.add(buttonPanel);
		
		quitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton source = (JButton)e.getSource();
				SwingUtilities.getWindowAncestor(source).dispose();
				System.exit(0);
			}
		});
		
		buttonPanel.add(quitButton);
		startButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JButton source = (JButton)e.getSource();
				InfoDialog dialog = (InfoDialog)SwingUtilities.getWindowAncestor(source);
				
				dialog.setVisible(false);
			}
		});
		
		buttonPanel.add(startButton);
		
		this.pack();
		this.setModal(true);
		this.setVisible(true);
	}
	
	public boolean useBot() {
		return botCheckbox.isSelected();
	}
	
	public boolean useSelenium() {
		return seleniumCheckbox.isSelected();
	}
	
	public String getToken() {
		return tokenField.getText();
	}
	
	public String getID() {
		return idField.getText();
	}
	
	public String getChromiumPath() {
		return installPathField.getText();
	}
	
	public String getOASISWebPath() {
		return OASISPathField.getText();
	}
	
	public ArrayList<String> getCRNs() {
		String areaContent = CRNArea.getText();
		String asArray[] = areaContent.split("\\r?\\n");
		ArrayList<String> asList = new ArrayList<>(Arrays.asList(asArray));
		return asList;
	}

}
