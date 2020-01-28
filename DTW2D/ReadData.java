import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class ReadData {
	DTW2D bd = new DTW2D(); // 버스 궤적 데이터
	DTW2D ud = new DTW2D(); // 유저 궤적 데이터

	public void getUD(int fileNum) { // 유저 궤적 데이터 불러오기
		try {
			String lineReader;
			FileInputStream fis = new FileInputStream(new File("USER\\usertraj_" + fileNum + ".txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			while ((lineReader = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(lineReader, ","); // 시각, x좌표, y좌표 분리
				ud.time.add(1000000 + Long.parseLong(st.nextToken().replace(":", ""))); // 시각
				ud.x.add(Double.parseDouble(st.nextToken())); // x좌표
				ud.y.add(Double.parseDouble(st.nextToken())); // y좌표
			}
			ud.TimeDataPreprocessing();

			br.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("usertraj_" + fileNum + "를 찾을 수 없음");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void getBD() { // 버스 궤적 데이터 불러오기
		try {
			String lineReader;
			int t;
			FileInputStream fis = new FileInputStream(new File("BUS\\bustraj.txt"));
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));

			while ((lineReader = br.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(lineReader, ","); // 시각, x좌표, y좌표 분리
				bd.time.add(Long.parseLong(st.nextToken().substring(6, 14))); // 시각
				t = bd.time.size();
				if (t > 2) { // 중복 데이터 제거
					if (bd.time.get(t - 2).compareTo(bd.time.get(t - 1)) == 0) { 
						bd.time.remove(t - 1);
						continue;
					}
				}
				bd.x.add(Double.parseDouble(st.nextToken())); // x좌표
				bd.y.add(Double.parseDouble(st.nextToken())); // y좌표
			}
			
			bd.TimeDataPreprocessing();

			br.close();
			fis.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("버스 데이터 파일 없음");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void start() { // 데이터 분석 시작
		final double referenceValue = 5; // 승객인지 확인하는 값
		double D; // 거리 저장
		int fileNum; // 파일 순서
		ArrayList<Integer> P = new ArrayList<Integer>(); // 버스 승객 목록
		long startTime;
		long endTime;

		getBD(); // 버스 궤적 데이터 불러오기

		startTime = System.nanoTime();
		for (fileNum = 1; fileNum < 21; fileNum++) { // 유저 궤적 데이터 반복

			D = compareData(fileNum); // 버스와 유저 거리 계산

			if (D < referenceValue) { // 버스 승객인지 확인
				P.add(fileNum); // 버스 승객 저장
			}

			System.out.println("fileNum = " + fileNum + "\tD = " + D);
		}
		endTime = System.nanoTime();
		
		System.out.println("\n버스 승객 파일 목록");
		for (int i : P) { // 버스 승객 목록 출력
			System.out.println(i);
		}
		System.out.println("\n실행 시간 : " + (endTime - startTime) + "ns");
	}

	public double compareData(int fileNum) {// 버스와 유저 거리 계산
		int index;
		double D;
		ud.x.clear(); // x, y 좌표 초기화
		ud.y.clear();
		ud.time.clear();
		getUD(fileNum); // 유저 궤적 데이터 불러오기

		D = 99999.9; // 거리 초기화
		for (index = 0; index < bd.x.size() - ud.x.size() - 1;) { // BD의 끝까지 UD가 이동하며 비교
			ud.makeMatrix(bd, index); // 초기 거리 저장 메소드
			ud.warpingDistance(D); // warping distance 계산
			D = Math.min(ud.result(), D); // 작은 값 저장

			index += 10; // 10번째 데이터마다 dtw 확인
		}
		return D;
	}

	public static void main(String[] args) {
		new ReadData().start();
	}
}
