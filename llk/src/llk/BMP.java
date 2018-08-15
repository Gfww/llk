package llk;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * @author ��
 * 
 */
public class BMP {

	private int width;
	private int height;
	private byte[] data;

	public BMP() {

	}

	public BMP(String src) {
		this.read(src);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	//���ֽ�ͨ��������   �� ��λ����  ƴ��Ϊ��Ӧֵ  18---21�ֽڴ�ͼƬ���  22---25�ֽڴ�ͼƬ�߶�   ��ϸ�����뿴    http://itindex.net/detail/50025-java-bmp-ͼƬ   
	public static int b2i(byte[] b, int s) {
		int ret = 0;
		for (int i = 0; i < 4; i++) {
			int temp = b[s + i] & 0xff;
			ret += temp << (8 * i);
		}
		return ret;
	}

	/** * ��ȡͼƬ�ļ� * @param src �ļ�·�� */
	public void read(String src) {
		width = 0;
		height = 0;
		ByteArrayOutputStream bs = new ByteArrayOutputStream();
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(src));
			byte[] b = new byte[1024 * 1024];
			int len = 0;
			while ((len = in.read(b)) != -1) {
				bs.write(b, 0, len);
				bs.flush();
			}
			data = bs.toByteArray();   //       ��ϸ�����뿴    http://itindex.net/detail/50025-java-bmp-ͼƬ 
			width = b2i(data, 18);     //18---21�ֽڴ�ͼƬ���           ע����������Ϊ��λ  
			height = b2i(data, 22);    //22---25�ֽڴ�ͼƬ�߶�
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bs.close();
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// �ⷽ���������Һþò�Ū����
	public int getColor(int x, int y) {
		// BMPͼҪ��ÿ���ֽ���Ϊ4�ı���,���������1-3�������ֽ�  
		
		int lineW = 0;//ÿ���ֽ���
		
		switch ((width * 3) % 4) {  //BMPͼƬwidth��������Ϊ��λ����һ������3���ֽ�
		case 0:
			lineW = width * 3;
			break;
		case 1:
			lineW = width * 3 + 3;
			break;
		case 2:
			lineW = width * 3 + 2;
			break;
		case 3:
			lineW = width * 3 + 1;
		}
		int i = 54 + (height - y - 1) * lineW + 3 * x;  //ͨ��һ����ʽ�õ��õ������λ��  ���������ֽڣ�ÿ���ֽڱ�ʾһ����ɫ r g b  
		int r = data[i + 2] & 0xff;
		int g = data[i + 1] & 0xff;
		int b = data[i] & 0xff;
		return r + (g << 8) + (b << 16);
	}

	public void setColor(int x, int y, int v) {
		int lineW = 0;
		switch ((width * 3) % 4) {
		case 0:
			lineW = width * 3;
			break;
		case 1:
			lineW = width * 3 + 3;
			break;
		case 2:
			lineW = width * 3 + 2;
			break;
		case 3:
			lineW = width * 3 + 1;
		}
		int i = 54 + (height - y - 1) * lineW + 3 * x;
		data[i + 2] = (byte) ((v >> 16) & 0xff);
		data[i + 1] = (byte) ((v >> 8) & 0xff);
		data[i] = (byte) (v & 0xff);
	}
	

	// ȡ������ɫ����
	public byte[] getData(int x, int y, int w, int h) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream(w * h);
		try {
			for (int i = x; i < x + w; i++) {
				for (int j = y; j < y + h; j++) {
				//	System.out.println(i + "," + j + " ��ɫΪ��" + getColor(i, j));
					bos.write(getColor(i, j));
					bos.flush();
				}
			}
			bos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return bos.toByteArray();
	}

}




/**
 * ��ȡBMP�ļ��ķ���(BMP24λ)
 *//*

public int[][] readFile(String path) {

	try {
		// ������ȡ�ļ����ֽ���
		FileInputStream fis = new FileInputStream(path);
		BufferedInputStream bis = new BufferedInputStream(fis);
		// ��ȡʱ����ǰ���18λ��
		// ��ȡͼƬ��18~21�Ŀ��
		bis.skip(18);
		byte[] b = new byte[4];
		bis.read(b);
		// ��ȡͼƬ�ĸ߶�22~25
		byte[] b2 = new byte[4];
		bis.read(b2);

		// �õ�ͼƬ�ĸ߶ȺͿ��
		int width = byte2Int(b);
		int heigth = byte2Int(b2);
		// ʹ�����鱣���ͼƬ�ĸ߶ȺͿ��
		int[][] date = new int[heigth][width];

		int skipnum = 0;
		if (width * 3 / 4 != 0) {
			skipnum = 4 - width * 3 % 4;
		}
		// ��ȡλͼ�е����ݣ�λͼ������ʱ��54λ��ʼ�ģ��ڶ�ȡ����ǰҪ����ǰ�������
		bis.skip(28);
		for (int i = 0; i < date.length; i++) {
			for (int j = 0; j < date[i].length; j++) {
				// bmp��ͼƬ��window������3��byteΪһ������
				int blue = bis.read();
				int green = bis.read();
				int red = bis.read();
				// ����һ��Color���󣬽�rgb��Ϊ������������
				Color c = new Color(red, green, blue);
				// Color c = new Color(blue,green,red);
				// ���õ������ر��浽date������
				date[i][j] = c.getRGB();
			}
			// �����0�ĸ�����Ϊ0������Ҫ������Щ���ϵ�0
			if (skipnum != 0) {
				bis.skip(skipnum);
			}
		}
		return date;
	} catch (Exception e) {
		e.printStackTrace();

	}
	return null;

}

// ���ĸ�byteƴ�ӳ�һ��int
public int byte2Int(byte[] by) {
	int t1 = by[3] & 0xff;
	int t2 = by[2] & 0xff;
	int t3 = by[1] & 0xff;
	int t4 = by[0] & 0xff;
	int num = t1 << 24 | t2 << 16 | t3 << 8 | t4;
	return num;

}*/
