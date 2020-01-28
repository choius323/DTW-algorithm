import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ReadData {
	DTW2D bd = new DTW2D(); // ���� ���� ������
	DTW2D ud = new DTW2D(); // ���� ���� ������

	public void getUD(int fileNum) { // ���� ���� ������ �ҷ�����
		try {
			String lineReader;
			FileInputStream fis = new FileInputStream(new File("USER\\usertraj_" + fileNum + ".txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			while ((lineReader = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(lineReader, ","); // �ð�, x��ǥ, y��ǥ �и�
				ud.time.add(1000000 + Long.parseLong(st.nextToken().replace(":", ""))); // �ð�
				ud.x.add(Double.parseDouble(st.nextToken())); // x��ǥ
				ud.y.add(Double.parseDouble(st.nextToken())); // y��ǥ
			}
			ud.TimeDataPreprocessing();

			br.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("usertraj_" + fileNum + "�� ã�� �� ����");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getBD() { // ���� ���� ������ �ҷ�����
		try {
			String lineReader;
			int t;
			FileInputStream fis = new FileInputStream(new File("BUS\\bustraj.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			while ((lineReader = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(lineReader, ","); // �ð�, x��ǥ, y��ǥ �и�
				bd.time.add(Long.parseLong(st.nextToken().substring(6, 14))); // �ð�
				t = bd.time.size();
				if (t > 2) { // �ߺ� ������ ����
					if (bd.time.get(t - 2).compareTo(bd.time.get(t - 1)) == 0) { 
						bd.time.remove(t - 1);
						continue;
					}
				}
				bd.x.add(Double.parseDouble(st.nextToken())); // x��ǥ
				bd.y.add(Double.parseDouble(st.nextToken())); // y��ǥ
			}
			
			bd.TimeDataPreprocessing();

			br.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("���� ������ ���� ����");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() { // ������ �м� ����
		final double referenceValue = 5; // �°����� Ȯ���ϴ� ��
		double D; // �Ÿ� ����
		int fileNum; // ���� ����
		ArrayList<Integer> P = new ArrayList<Integer>(); // ���� �°� ���
		long startTime;
		long endTime;

		getBD(); // ���� ���� ������ �ҷ�����

		startTime = System.nanoTime();
		for (fileNum = 1; fileNum < 21; fileNum++) { // ���� ���� ������ �ݺ�

			D = compareData(fileNum); // ������ ���� �Ÿ� ���

			if (D < referenceValue) { // ���� �°����� Ȯ��
				P.add(fileNum); // ���� �°� ����
			}

			System.out.println("fileNum = " + fileNum + "\tD = " + D);
		}
		endTime = System.nanoTime();
		
		System.out.println("\n���� �°� ���� ���");
		for (int i : P) { // ���� �°� ��� ���
			System.out.println(i);
		}
		System.out.println("\n���� �ð� : " + (endTime - startTime) + "ns");
	}

	public double compareData(int fileNum) {// ������ ���� �Ÿ� ���
		int index;
		double D;
		ud.x.clear(); // x, y ��ǥ �ʱ�ȭ
		ud.y.clear();
		ud.time.clear();
		getUD(fileNum); // ���� ���� ������ �ҷ�����

		D = 99999.9; // �Ÿ� �ʱ�ȭ
		for (index = 0; index < bd.x.size() - ud.x.size() - 1;) { // BD�� ������ UD�� �̵��ϸ� ��
			ud.makeMatrix(bd, index); // �ʱ� �Ÿ� ���� �޼ҵ�
			ud.warpingDistance(D); // warping distance ���
			D = Math.min(ud.result(), D); // ���� �� ����

			index += 10; // 10��° �����͸��� dtw Ȯ��
		}
		return D;
	}

	public static void main(String[] args) {
		new ReadData().start();
	}
}
