import java.awt.EventQueue;
import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuItem;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.JButton;
import javax.swing.JLabel;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;

import javax.swing.JSlider;

import java.awt.Font;
import java.awt.Color;

import javax.swing.JCheckBox;
import javax.swing.JSeparator;


public class Main extends JFrame implements Runnable {

	private JPanel contentPane;
	private Clip clip;
	public static Main main = new Main();
	private static int x,y;
	private boolean isStarted=false,rep=false,displayedInfo=false;
	private static boolean hasVal=false;
	private JButton btnStartSound = new JButton("Start Sound");
	private float volume = -1.0f;
	private JSlider slider = new JSlider();
	private final MenuItem item = new MenuItem("Minimize");
	private JLabel lblNewLabel_2 = new JLabel("+0 dB"),lblNewLabel_3 = new JLabel("+0 dB");;
	private JCheckBox chckbxNewCheckBox = new JCheckBox("Automatically start Silent Audio at:");
	private static int sliderValue = 20;
	private static String loadDetails = "false";
	private JLabel stopLoad = new JLabel("false");
	public boolean stopped = false;
	public File tempFile = null;

	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					main.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public Main() {
		reloadFrame();
	}
	
	public void reloadFrame(){
		setUndecorated(true);
		File f = new File(System.getProperty("user.home")+"\\.SilAud\\settings.cfg");
		if(f.exists()&&!hasVal)
		try{
			BufferedReader bf = new BufferedReader(new FileReader(System.getProperty("user.home")+"\\.SilAud\\settings.cfg"));
			String line="";
			while((line=bf.readLine())!= null){
				if(line.startsWith("$dB_Val: ")){
					sliderValue = Integer.parseInt(line.split(" ")[1]);
					loadDetails="true";
					chckbxNewCheckBox.setSelected(true);
					displayedInfo=true;
					hasVal=true;
				}
			}
		}catch(Exception e){}
		stopLoad.setText(loadDetails);
		if(stopLoad.getText()=="false"){
			setVisible(true);
			this.setOpacity(0.75f);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(800, 500, 450, 238);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		btnStartSound.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if(!isStarted){
					isStarted=true;
					rep=true;
					volume = Float.parseFloat(Integer.toString(slider.getValue()-20));
					new Thread(main).start();
					btnStartSound.setText("Stop Sound");
				}else{
	                clip.stop();
	                clip.flush();
	                clip.close();
					isStarted=false;
					rep=false;
					btnStartSound.setText("Start Sound");
				}
			}
		});
		btnStartSound.setBounds(0, 80, 225, 29);
		contentPane.add(btnStartSound);
		
		JButton btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
					main.fadeOut(main, 0.75f, 10);
					System.exit(0);
			}
		});
		btnClose.setBounds(219, 80, 231, 29);
		contentPane.add(btnClose);
		
		JLabel lblStartsAhz = new JLabel("The Application starts a 10Hz sound (Nearly impossible to hear).");
		lblStartsAhz.setBounds(6, 6, 407, 16);
		contentPane.add(lblStartsAhz);
		
		JLabel lblNewLabel = new JLabel("The Application prevents Audio Output from closing.");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		lblNewLabel.setBounds(6, 62, 334, 16);
		contentPane.add(lblNewLabel);
		slider.setValue(sliderValue+20);
		slider.setSnapToTicks(true);
		slider.setMinimum(0);
		slider.setMaximum(40);
		slider.setBounds(0, 121, 450, 29);
		contentPane.add(slider);
		
		MenuBar menuBar=new MenuBar();
		Menu file = new Menu("View");
		file.add(item);
		menuBar.add(file);
		setMenuBar(menuBar); 
		
		item.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				main.fadeOut(main, 0.75f, 10);
				main.setOpacity(0.0f);
				setState(Frame.ICONIFIED);
			}
		});
		
		JLabel lblDb = new JLabel("+0 dB");
		lblDb.setBounds(204, 146, 37, 16);
		contentPane.add(lblDb);
		
		JLabel lbldb = new JLabel("-20 dB");
		lbldb.setBounds(6, 146, 61, 16);
		contentPane.add(lbldb);
		
		JLabel lblDb_1 = new JLabel("+20 dB");
		lblDb_1.setBounds(399, 146, 45, 16);
		contentPane.add(lblDb_1);
		
		JLabel lblSoundsOf = new JLabel("Sounds of +1 dB and over will be hearable (Use audio slider to adjust).");
		lblSoundsOf.setBounds(6, 20, 444, 16);
		contentPane.add(lblSoundsOf);
		
		JLabel lblAudioSlider = new JLabel("Audio Slider");
		lblAudioSlider.setFont(new Font("Lucida Grande", Font.BOLD, 13));
		lblAudioSlider.setBounds(183, 108, 86, 16);
		contentPane.add(lblAudioSlider);
		
		JLabel lblUseView = new JLabel("Use View -> Hide in the MenuBar to hide this window and");
		lblUseView.setBounds(6, 34, 444, 16);
		contentPane.add(lblUseView);
		
		JLabel lblNewLabel_1 = new JLabel("View -> Show to make it re-appear.");
		lblNewLabel_1.setBounds(6, 48, 231, 16);
		contentPane.add(lblNewLabel_1);
		
		JLabel lblCurrentAudioLevel = new JLabel("Current Audio Level:");
		lblCurrentAudioLevel.setBounds(6, 162, 128, 16);
		contentPane.add(lblCurrentAudioLevel);
		
		lblNewLabel_2.setForeground(Color.BLUE);
		lblNewLabel_2.setBounds(141, 162, 51, 16);
		contentPane.add(lblNewLabel_2);
		
		chckbxNewCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(chckbxNewCheckBox.isSelected()&&!displayedInfo){
					JOptionPane.showMessageDialog(contentPane, "Please note:\n" +
							"\u2022 Selecting this option makes the program start\n" +
							"automatically at boot with your current preset dB value. (Updates every 500ms)\n" +
							"\u2022 The program starts hidden and will have to be accessed\n" +
							"by clicking on the Dock menu icon and selecting 'View -> Show' in the MenuBar.\n" +
							"\u2022 Using this option WILL store a file on the Hard Drive, make sure the\n" +
							"application has access to '"+System.getProperty("user.home")+"\\'"+", the setting\n" +
							"file will be stored in a folder '\\.SilAud\\settings.cfg\\.\n" +
							"(You can remove this file and folder by simply unselecting the check box.)", "Info", JOptionPane.INFORMATION_MESSAGE);
					displayedInfo=true;
				}
				if(chckbxNewCheckBox.isSelected()){
					File f = new File(System.getProperty("user.home")+"\\.SilAud\\settings.cfg");
					File f2 = new File(System.getProperty("user.home")+"\\.SilAud\\");
					if(!f2.exists())
						f2.mkdir();
					else
						f2.delete();
					try {
						int value = slider.getValue()-20;
						PrintWriter pw = new PrintWriter(System.getProperty("user.home")+"\\.SilAud\\settings.cfg");
						pw.println("#This is the setting file for Silent Audio, do NOT edit anything besides parameter values and comment lines.");
						pw.println("#Parameters are line that begin with a '$', while lines that begin with a '#' are comment lines.");
						pw.println("#If this file is deleted you WILL break Silent Audio's auto-start function.");
						pw.println("#----------------------------------------------BEGIN PARAMETERS---------------------------------------------");
						pw.println("#Decibel value:");
						pw.println("$dB_Val: "+value);
						pw.close();
					} catch (FileNotFoundException e1) {}
					try {
						chckbxNewCheckBox.setEnabled(false);
						Thread.sleep(1000);
						chckbxNewCheckBox.setEnabled(true);
					} catch (InterruptedException e1) {}
				}
				else if(!chckbxNewCheckBox.isSelected()){
					File f = new File(System.getProperty("user.home")+"\\.SilAud\\settings.cfg");
					File f2 = new File(System.getProperty("user.home")+"\\.SilAud\\");
					if(f2.isDirectory()){
					}
					if(f.exists()){
						f.delete();
						try {
							Thread.sleep(100);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						f2.delete();
					}
					try {
						chckbxNewCheckBox.setEnabled(false);
						Thread.sleep(1000);
						chckbxNewCheckBox.setEnabled(true);
					} catch (InterruptedException e1) {}
				}
			}
		});
		chckbxNewCheckBox.setBounds(66, 190, 252, 23);
		contentPane.add(chckbxNewCheckBox);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBounds(0, 179, 450, 12);
		contentPane.add(separator);
		
		lblNewLabel_3.setForeground(Color.BLUE);
		lblNewLabel_3.setBounds(319, 194, 73, 16);
		contentPane.add(lblNewLabel_3);
		
		new Thread(new Runnable(){
			public void run(){
				while(true){
					try{
						Thread.sleep(5);
						int value = slider.getValue()-20;
							if(value<0){
								lblNewLabel_2.setText(value+" dB");
								lblNewLabel_3.setText(value+" dB");
							}
							else{
								lblNewLabel_2.setText("+"+value+" dB");
								lblNewLabel_3.setText("+"+value+" dB");
							}
					}catch(Exception e){}
				}
			}
		}).start();
		
		new Thread(new Runnable(){
			public void run(){
				while(true){
					try{
						Thread.sleep(500);
						int value = slider.getValue()-20;
							if(chckbxNewCheckBox.isSelected()){
								PrintWriter pw = new PrintWriter(System.getProperty("user.home")+"\\.SilAud\\settings.cfg");
								pw.println("#This is the setting file for Silent Audio, do NOT edit anything besides parameter values and comment lines.");
								pw.println("#Parameters are line that begin with a '$', while lines that begin with a '#' are comment lines.");
								pw.println("#If this file is deleted you WILL break Silent Audio's auto-start function.");
								pw.println("#----------------------------------------------BEGIN PARAMETERS---------------------------------------------");
								pw.println("#Decibel value:");
								pw.println("$dB_Val: "+value);
								pw.close();
							}
					}catch(Exception e){}
				}
			}
		}).start();
		
		addMouseListener(new MouseAdapter() {  
			public void mousePressed(MouseEvent e) {  
			if(!e.isMetaDown()){  
			x = e.getX();  
			y = e.getY();  
			}  
			}  
			});  
		addMouseMotionListener(new MouseMotionAdapter() {  
			public void mouseDragged(MouseEvent e) {  
			if(!e.isMetaDown()){  
			Point p = getLocation();  
			setLocation(p.x + e.getX() - x,  
			p.y + e.getY() - y);  
			}  
			}  
			});
		new Thread(new Runnable(){
			public void run(){
				if(stopLoad.getText()!="true"){
					main.fadeIn(main, 0.75f, 10);
					return;
				}
				isStarted=true;
				rep=true;
				volume = Float.parseFloat(Integer.toString(slider.getValue()-20));
				new Thread(main).start();
				btnStartSound.setText("Stop Sound");
			}
		}).start();
	}
	
	private void fadeOut(JFrame window,float currentOpacity,long sleepTime){
		for(float f=currentOpacity;f>=0.02f;f-=0.01f){
			window.setOpacity(f);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e1) {}
		}
	}
	
	private void fadeIn(JFrame window,float peakOpacity,long sleepTime){
		for(float f=0.01f;f<=peakOpacity;f+=0.01f){
			window.setOpacity(f);
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {}
		}
	}

	@Override
	public void run() {
        try{
        InputStream in = Main.class.getResourceAsStream("10hz.wav");
        tempFile = File.createTempFile("tempfile", ".wav");
        tempFile.deleteOnExit();  
        FileOutputStream out = new FileOutputStream(tempFile);
    byte[] buffer = new byte[1024];
    int bytesRead;
    while ((bytesRead = in.read(buffer)) != -1)
    {
        out.write(buffer, 0, bytesRead);
    }
    clip = (Clip) AudioSystem.getLine(new Line.Info(Clip.class));
    AudioInputStream audioStream = AudioSystem.getAudioInputStream(tempFile);
    clip.open(audioStream);
    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    gainControl.setValue(volume-14);
    clip.loop(clip.LOOP_CONTINUOUSLY);
        }catch(Exception e){e.printStackTrace();}
	}
	
	
	/*
	 * UNUSED!
	 * 

	void play(String filename){

        int total, totalToRead, numBytesRead, numBytesToRead;
        byte[] buffer;
        AudioFormat     wav;
        TargetDataLine  line;
        SourceDataLine  lineIn;
        DataLine.Info   info;
        File            file;
        FileInputStream fis;

        wav = new AudioFormat(44100, 16, 2, true, false);
        info = new DataLine.Info(SourceDataLine.class, wav);


        buffer = new byte[1024*333];
        numBytesToRead = 1024*333;
        total=0;
        stopped = false;

        if (!AudioSystem.isLineSupported(info)) {
            System.out.print("no support for " + wav.toString() );
        }
        try {
            // Obtain and open the line.
            lineIn = (SourceDataLine) AudioSystem.getLine(info);
            lineIn.open(wav);
            lineIn.start();
            fis = new FileInputStream(file = new File(filename));
            totalToRead = fis.available();



            while (total < totalToRead && !stopped){
                numBytesRead = fis.read(buffer, 0, numBytesToRead);
                if (numBytesRead == -1) break;
                total += numBytesRead;
                lineIn.write(buffer, 0, numBytesRead);
            }

        } catch (LineUnavailableException ex) {
            ex.printStackTrace();
        } catch (FileNotFoundException nofile) {
            nofile.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }
	}
	
	 */
	
	
}
