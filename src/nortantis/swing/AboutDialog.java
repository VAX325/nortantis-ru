package nortantis.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import nortantis.MapSettings;
import nortantis.platform.awt.AwtFactory;
import nortantis.util.Assets;
import nortantis.util.Localization;

@SuppressWarnings("serial")
public class AboutDialog extends JDialog
{
	public AboutDialog(MainWindow mainWindow)
	{
               super(mainWindow, Localization.get("#AboutTitle"), Dialog.ModalityType.APPLICATION_MODAL);
		setResizable(false);
		setLayout(new BorderLayout());
		JPanel content = new JPanel();
		add(content, BorderLayout.CENTER);
		content.setLayout(new BorderLayout());
		content.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
		BufferedImage nortantisImage = AwtFactory
				.unwrap(Assets.readImage(Paths.get(Assets.getAssetsPath(), "internal", "taskbar icon medium size.png").toString()));
		content.add(new ImagePanel(nortantisImage), BorderLayout.WEST);

		JPanel rightPanel = new JPanel();
		rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
		rightPanel.setPreferredSize(new Dimension(nortantisImage.getWidth(), nortantisImage.getHeight()));
               JLabel text = new JLabel(Localization.get("#AboutVersion").replace("{version}", MapSettings.currentVersion));
		rightPanel.add(text);

		rightPanel.add(new JLabel(" "));

               rightPanel.add(new JLabel(Localization.get("#BugReportPrompt")));
		rightPanel
				.add(SwingHelper.createHyperlink("github.com/jeheydorn/nortantis/issues", "https://github.com/jeheydorn/nortantis/issues"));

		rightPanel.add(new JLabel(" "));
               rightPanel.add(new JLabel(Localization.get("#SupportPrompt")));
		rightPanel.add(SwingHelper.createHyperlink("jandjheydorn.com/", "https://jandjheydorn.com/"));

		rightPanel.add(Box.createVerticalGlue());

		content.add(rightPanel, BorderLayout.EAST);

		JPanel bottomPanel = new JPanel();
		content.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
               JButton closeButton = new JButton(Localization.get("#CloseButton"));
		closeButton.setMnemonic(KeyEvent.VK_C);
		closeButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		bottomPanel.add(closeButton);

		pack();
	}

}
