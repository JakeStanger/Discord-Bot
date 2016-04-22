package window;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

import bot.Bot;
import util.ReadWrite;

public class Window implements ActionListener
{
	private JFrame frame = new JFrame("Grandad Botbags");
	private JTextField txtToken;
	private JButton btnStart;
	private JCheckBox chkRemember;
	
	//private ResourceLocation tokenLocation = new ResourceLocation("/token.txt");
	
	public Window()
	{
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.X_AXIS));
		frame.setIconImage(new ImageIcon("images/icon.png").getImage());
		
		JPanel pnlMain = new JPanel();
		frame.getContentPane().add(pnlMain);
		GridBagLayout gbl_pnlMain = new GridBagLayout();
		gbl_pnlMain.columnWidths = new int[]{497, 0};
		gbl_pnlMain.rowHeights = new int[]{45, 475, 0};
		gbl_pnlMain.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gbl_pnlMain.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		pnlMain.setLayout(gbl_pnlMain);
		
		JPanel pnlControl = new JPanel();
		GridBagConstraints gbc_pnlControl = new GridBagConstraints();
		gbc_pnlControl.fill = GridBagConstraints.BOTH;
		gbc_pnlControl.insets = new Insets(0, 0, 5, 0);
		gbc_pnlControl.gridx = 0;
		gbc_pnlControl.gridy = 0;
		pnlMain.add(pnlControl, gbc_pnlControl);
		
		JLabel lblToken = new JLabel("Token:");
		pnlControl.add(lblToken);
		
		txtToken = new JTextField();
		pnlControl.add(txtToken);
		txtToken.setColumns(10);
		
		chkRemember = new JCheckBox("Remember");
		chkRemember.setSelected(true);
		pnlControl.add(chkRemember);
		
		btnStart = new JButton("Start Server");
		pnlControl.add(btnStart);
		
		JPanel pnlConsole = new JPanel();
		GridBagConstraints gbcPnlConsole = new GridBagConstraints();
		gbcPnlConsole.fill = GridBagConstraints.BOTH;
		gbcPnlConsole.gridx = 0;
		gbcPnlConsole.gridy = 1;
		pnlMain.add(pnlConsole, gbcPnlConsole);
		pnlConsole.setLayout(new CardLayout(0, 0));
		pnlConsole.setPreferredSize(new Dimension(50, 500));
		
		JTextPane txtConsole = new JTextPane();
		JScrollPane scrollPane = new JScrollPane(txtConsole);
		pnlConsole.add(scrollPane);
		
		MessageConsole mc = new MessageConsole(txtConsole);
		mc.redirectOut();
		mc.redirectErr(Color.RED, null);
		mc.setMessageLines(100);
		
		frame.setVisible(true);
		frame.setSize(new Dimension(500, 550));
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		
		this.btnStart.addActionListener(this);
		
		String token = ReadWrite.readTokenFromFile();
		if(token != null) this.txtToken.setText(token);
	}

	@Override
	public void actionPerformed(ActionEvent event)
	{
		if(event.getSource() == this.btnStart)
		{
			System.out.println("-----------------------------------------------STARTING SERVER-----------------------------------------------");
			boolean success = Bot.getInstance().init(this.txtToken.getText());
			if(success)
			{
				this.btnStart.setEnabled(false);
				if(this.chkRemember.isSelected()) ReadWrite.writeTokenToFile(this.txtToken.getText());
			}
		}
	}
}
