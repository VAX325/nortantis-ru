package nortantis.swing;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileSystemView;

import nortantis.IconType;
import nortantis.MapSettings;
import nortantis.editor.UserPreferences;
import nortantis.util.Assets;
import nortantis.util.FileHelper;
import nortantis.util.Logger;
import nortantis.util.OSHelper;
import nortantis.util.Localization;

@SuppressWarnings("serial")
public class CustomImagesDialog extends JDialog
{
	private JTextField customImagesFolderField;

	public CustomImagesDialog(MainWindow mainWindow, String currentCustomImagesPath, Consumer<String> storeResult)
	{
		super(mainWindow, Localization.get("#CustomImagesFolder"), Dialog.ModalityType.APPLICATION_MODAL);
		setSize(OSHelper.isWindows() ? new Dimension(860, 750) : new Dimension(1020, 840));
		JPanel content = new JPanel();
		add(content);
		content.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		content.setLayout(new BorderLayout());

		int space = 6;

		GridBagOrganizer organizer = new GridBagOrganizer();
		content.add(organizer.panel, BorderLayout.CENTER);
		organizer.addLeftAlignedComponent(
				new JLabel(Localization.get("#CustomImagesFolderInfo", Assets.customArtPack)),
				space, space, false);

		int spaceBetweenPaths = 2;
		organizer.addLeftAlignedComponent(
				new JLabel(Localization.get("#CustomImagesFolderStructureBG", File.separator)),
				space, spaceBetweenPaths, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderStructureBorders", File.separator, File.separator, File.separator)), space, spaceBetweenPaths, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderStructureCities", File.separator, File.separator, File.separator)),
				spaceBetweenPaths, spaceBetweenPaths, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderStructureDecorations", File.separator, File.separator, File.separator)), spaceBetweenPaths, spaceBetweenPaths, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderStructureHills", File.separator, File.separator, File.separator)),
				spaceBetweenPaths, spaceBetweenPaths, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderStructureMountains", File.separator, File.separator, File.separator)), spaceBetweenPaths, spaceBetweenPaths, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderStructureSand", File.separator, File.separator, File.separator)), spaceBetweenPaths, spaceBetweenPaths, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderStructureTrees", File.separator, File.separator, File.separator)),
				spaceBetweenPaths, spaceBetweenPaths, false);

		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderAngleBracketsInfo")),
				space, space, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderIconSizingInfo")),
				space, space, false);
		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderBorderInfo")),
				space, space, false);

		organizer.addLeftAlignedComponent(
				new JLabel(Localization.get("#CustomImagesFolderTreeInfo")),
				space, space, false);

		organizer.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderHillInfo")),
				space, space, false);

		organizer
				.addLeftAlignedComponent(new JLabel(Localization.get("#CustomImagesFolderRefreshInfo", mainWindow.getFileMenuName(), mainWindow.getRefreshImagesMenuName())), space, space, false);
		organizer.addLeftAlignedComponent(
				new JLabel(Localization.get("#CustomImagesFolderRevertInfo")), space, 10,
				false);

		JButton openButton = new JButton(Localization.get("#Open"));

		customImagesFolderField = new JTextField();
		customImagesFolderField.getDocument().addDocumentListener(new DocumentListener()
		{

			@Override
			public void removeUpdate(DocumentEvent e)
			{
				openButton.setEnabled(!customImagesFolderField.getText().isEmpty());
			}

			@Override
			public void insertUpdate(DocumentEvent e)
			{
				openButton.setEnabled(!customImagesFolderField.getText().isEmpty());
			}

			@Override
			public void changedUpdate(DocumentEvent e)
			{
				openButton.setEnabled(!customImagesFolderField.getText().isEmpty());
			}
		});
		customImagesFolderField.setText(FileHelper.replaceHomeFolderPlaceholder(currentCustomImagesPath));
		JButton browseButton = new JButton(Localization.get("#Browse"));
		browseButton.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				File folder = new File(customImagesFolderField.getText());
				if (!folder.exists())
				{
					folder = FileSystemView.getFileSystemView().getDefaultDirectory();
				}
				JFileChooser folderChooser = new JFileChooser(folder);
				folderChooser.setDialogTitle(Localization.get("#SelectFolderDialogTitle"));
				folderChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int returnValue = folderChooser.showOpenDialog(null);
				if (returnValue == JFileChooser.APPROVE_OPTION)
				{
					customImagesFolderField.setText(folderChooser.getSelectedFile().toString());
				}
			}
		});

		openButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				File folder = new File(customImagesFolderField.getText());
				if (!folder.exists())
				{
					JOptionPane.showMessageDialog(null, Localization.get("#UnableToOpenNotExistingFolder", folder.getAbsolutePath()),
							Localization.get("#ErrorTitle"), JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (!folder.isDirectory())
				{
					JOptionPane.showMessageDialog(null, Localization.get("#UnableToOpenFileIsNotFolder", folder.getAbsolutePath()), Localization.get("#ErrorTitle"),
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				OSHelper.openFileExplorerTo(folder);
			}
		});
		openButton.setEnabled(!customImagesFolderField.getText().isEmpty());

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		panel.add(new JLabel(Localization.get("#CustomImagesFolderLabel")));
		panel.add(Box.createHorizontalStrut(10));
		panel.add(customImagesFolderField);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(browseButton);
		panel.add(Box.createHorizontalStrut(5));
		panel.add(openButton);

		organizer.addLeftAlignedComponent(panel, false);

		JCheckBox makeDefaultCheckbox = new JCheckBox(Localization.get("#MakeDefaultForNewMaps"));
		organizer.addLeftAlignedComponent(makeDefaultCheckbox);

		organizer.addVerticalFillerRow();

		JPanel bottomPanel = new JPanel();
		content.add(bottomPanel, BorderLayout.SOUTH);
		bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
		JButton okayButton = new JButton(Localization.get("#OKButton"));
		okayButton.setMnemonic(KeyEvent.VK_O);
		okayButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				boolean isChanged = !Objects.equals(customImagesFolderField.getText(),
						FileHelper.replaceHomeFolderPlaceholder(currentCustomImagesPath));
				if (mergeInstalledImagesIntoCustomFolderIfEmpty(customImagesFolderField.getText()))
				{
					JOptionPane.showMessageDialog(null,
							Localization.get("#InstalledImagesCopied", Paths.get(customImagesFolderField.getText()).toAbsolutePath()),
							Localization.get("#SuccessTitle"), JOptionPane.INFORMATION_MESSAGE);
				}
				else if (MapSettings.isOldCustomImagesFolderStructure(customImagesFolderField.getText()))
				{
					try
					{
						MapSettings.convertOldCustomImagesFolder(customImagesFolderField.getText());

						JOptionPane.showMessageDialog(null,
								Localization.get("#CustomImagesFolderConvertedMessage"),
								Localization.get("#CustomImagesFolderConvertedTitle"),  JOptionPane.INFORMATION_MESSAGE);
					}
					catch (IOException ex)
					{
						String errorMessage = Localization.get("#ErrorRestructuringCustomImages", customImagesFolderField.getText(), ex.getMessage());
						Logger.printError(errorMessage, ex);
						JOptionPane.showMessageDialog(null, errorMessage, Localization.get("#ErrorTitle"), JOptionPane.ERROR_MESSAGE);
					}
				}

				// If the custom images folder changed, then store the value, refresh images, and redraw the map.
				if (isChanged)
				{
					storeResult.accept(FileHelper.replaceHomeFolderWithPlaceholder(customImagesFolderField.getText()));
				}

				if (makeDefaultCheckbox.isSelected())
				{
					UserPreferences.getInstance().defaultCustomImagesPath = FileHelper
							.replaceHomeFolderWithPlaceholder(customImagesFolderField.getText());
				}

				dispose();
			}
		});
		bottomPanel.add(okayButton);

		JButton cancelButton = new JButton(Localization.get("#CancelButton"));
		cancelButton.setMnemonic(KeyEvent.VK_C);
		cancelButton.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				dispose();
			}
		});
		bottomPanel.add(cancelButton);
	}

	private boolean mergeInstalledImagesIntoCustomFolderIfEmpty(String customImagesFolder)
	{
		if (customImagesFolder == null || customImagesFolder.isEmpty())
		{
			return false;
		}

		File folder = new File(customImagesFolder);
		if (!folder.exists())
		{
			JOptionPane.showMessageDialog(null,
					Localization.get("#UnableToOpenNotExistingFolder", folder.getAbsolutePath()), Localization.get("#ErrorTitle"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}
		else if (!folder.isDirectory())
		{
			JOptionPane.showMessageDialog(null,
					Localization.get("#UnableToOpenFileIsNotFolder", folder.getAbsolutePath()), Localization.get("#ErrorTitle"),
					JOptionPane.ERROR_MESSAGE);
			return false;
		}

		boolean isFolderEmpty;
		try
		{
			isFolderEmpty = !Files.newDirectoryStream(folder.toPath()).iterator().hasNext();
		}
		catch (IOException ex)
		{
			JOptionPane.showMessageDialog(null, Localization.get("#ErrorWhileFolderEmptyCheck", folder.getAbsolutePath(), ex.getMessage()),
					Localization.get("#ErrorTitle"), JOptionPane.ERROR_MESSAGE);
			return false;
		}

		try
		{
			if (isFolderEmpty)
			{
				Assets.copyDirectoryToDirectory(Paths.get(Assets.getInstalledArtPackPath(), "background textures"), folder.toPath());
				Assets.copyDirectoryToDirectory(Paths.get(Assets.getInstalledArtPackPath(), "borders"), folder.toPath());
				for (IconType type : IconType.values())
				{
					Assets.copyDirectoryToDirectory(Paths.get(Assets.getInstalledArtPackPath(), type.toString()), folder.toPath());
				}
				return true;
			}
		}
		catch (IOException ex)
		{
			String message = Localization.get("#ErrorCopyingInstalledImages", folder.getAbsolutePath(), ex.getMessage());
			JOptionPane.showMessageDialog(this, message, Localization.get("#ErrorTitle"), JOptionPane.ERROR_MESSAGE);
			Logger.printError(message, ex);
		}

		return false;
	}
}
