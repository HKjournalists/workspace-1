/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.bupt.qunar.gisdata;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.*;
import java.util.List;
import org.apache.log4j.Logger;

import edu.bupt.utils.Directory;
import edu.bupt.utils.FileUtil;

/**
 *
 * @author oulong
 */
public class ImageCheckUI extends JFrame {

    private static Logger logger = Logger.getLogger(ImageCheckUI.class);
    private List<String> imagePathList = new ArrayList<String>();
    private String removedpath;
    private static String checkedformatting = "H:/Java work/longUtils/Image/checked/%s";
    private JPanel client;//中间客户区
    private JLabel pic;//图片标签
    private JLabel statebar, one, two, three;
    private JScrollPane imgSp;
    JButton checkedButton, nextButton;
    public static String windowsstyle = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";

    public ImageCheckUI() {
        super("Image Check UI Example (Swing)");

        try {
            UIManager.setLookAndFeel(windowsstyle);
        } catch (Exception e) {
            System.out.println("Style changing failed.");
        }

        addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        ActionListener printListener = new ActionListener() {

            public void actionPerformed(ActionEvent ae) {
                if (ae.getSource() == checkedButton) {
                    afterChecked();
                } else {
                    nextImage();
                }
            }
        };

        //初始化UI的时候导入需审核文件夹图片
        setUp();


        //添加图片展示容器
        client = new JPanel();
        add(client, BorderLayout.CENTER);
        client.setBorder(BorderFactory.createTitledBorder(""));

        //状态栏信息栏
        statebar = new JLabel();
        statebar.setLayout(new GridLayout(3, 1));
        one = new JLabel("像素大小： ",SwingConstants.LEFT);
        two = new JLabel("文件名称： ",SwingConstants.LEFT);
        three = new JLabel("文件大小： ",SwingConstants.LEFT);
//        statebar.add(one,BorderLayout.NORTH);
//        statebar.add(two,BorderLayout.CENTER);
//        statebar.add(three,BorderLayout.SOUTH);
        statebar.setBorder(BorderFactory.createLineBorder(Color.black));
//        statebar.setText("未选定");
        client.add(statebar, BorderLayout.NORTH);

        pic = new JLabel();
        client.add(pic, BorderLayout.SOUTH);
        pic.setSize(client.getWidth() , client.getHeight()-20);



        JPanel toolbar = new JPanel();
        toolbar.setLayout(new FlowLayout(FlowLayout.CENTER));

        checkedButton = new JButton("Checked");
        checkedButton.addActionListener(printListener);
        toolbar.add(checkedButton);

        nextButton = new JButton("Next");
        nextButton.addActionListener(printListener);
        toolbar.add(nextButton);

        getContentPane().add(toolbar, BorderLayout.SOUTH);

        imgSp = new JScrollPane();
        imgSp.setViewportView(client);
        imgSp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        imgSp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        getContentPane().add(imgSp, BorderLayout.CENTER);

        setSize(900, 650);//窗口大小
        //设置位置
//        setLocation(Toolkit.getDefaultToolkit().getScreenSize().width / 2 - 350, 150);
        setLocation(150, 50);
        openfile();
    }

    private void setUp() {
        File[] temp = Directory.local("H:\\Java work\\longUtils\\Image\\baidu\\", ".*?\\.jpg");
        for (int i = 0; i < temp.length; i++) {
            imagePathList.add(temp[i].getAbsolutePath());
        }
    }

    private void openfile() {
        String firstpath = imagePathList.get(0);
        File f = new File(firstpath);
        ImageIcon img = new ImageIcon(firstpath);
        pic.setIcon(img);


//        one.setText("像素大小： " + img.getIconWidth() + "*" + img.getIconHeight());
//        two.setText("文件名称： " + f.getName() );
//        three.setText("文件大小： " + f.length() / 1024 + "KB");

        statebar.setText("像素大小： " + img.getIconWidth() + "*" + img.getIconHeight()
                + "   文件名称： " + f.getName() + "   文件大小： " + f.length() / 1024 + "KB");
        removedpath = imagePathList.remove(0);
    }

    //确认ok后 将图片移到checked文件夹 删除原来图片 显示下一张
    private void afterChecked() {
        File f = new File(removedpath);
        String checkedpath = String.format(checkedformatting, f.getName());
        try {
            FileUtil.copyJPEG(removedpath, checkedpath,false);
        } catch (IOException ex) {
            logger.error("JPG copy erro.", ex);
        }
        FileUtil.forceDelete(f);
        openfile();
    }

    //图片显示部分显示下一张图片 删除原有图片
    private void nextImage() {
        File f = new File(removedpath);
        FileUtil.forceDelete(f);
        openfile();
    }

    public static void main(String[] args) {
        ImageCheckUI UI = new ImageCheckUI();
        UI.setVisible(true);
    }
}
