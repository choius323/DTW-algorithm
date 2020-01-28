import java.lang.Math;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DTW2D {
	// fileds
	ArrayList<Double> x = new ArrayList<Double>(); // x좌표
	ArrayList<Double> y = new ArrayList<Double>(); // y좌표
	double[][] dtw; // 거리 저장
	ArrayList<Long> time = new ArrayList<Long>(); // 시각
	int match;

	// mothods
	public void makeMatrix(DTW2D BD, int index) { // 초기 거리 저장 메소드
		int i = 0, j = index;
		match = 0; // 시간이 비슷한 정도
		ArrayList<Integer> matchTimeUD = new ArrayList<Integer>();
		ArrayList<Integer> matchTimeBD = new ArrayList<Integer>();

		long ti = 0, tj = 0;
		while (i < time.size() - 1 & j < BD.time.size() - 1) { // 비슷한 시간 확인
			if (Math.abs(tj - ti) < 3) { // 비슷함
				match += 1;
				matchTimeUD.add(i);
				matchTimeBD.add(j);
				ti = time.get(++i);
				tj = BD.time.get(++j);
			} else if (tj < ti) {
				tj += BD.time.get(++j);
			} else {
				ti += time.get(++i);
			}
		}
		
		dtw = new double[match][match]; // dtw 배열 생성
		i = 0;
		for (int a : matchTimeUD) {
			j=0;
			for (int b : matchTimeBD) {
				dtw[i][j++] = distance(x.get(a), BD.x.get(b), y.get(a), BD.y.get(b)) * 100; // 점 사이의 거리 측정
			}
			i++;
		}
	}

	public void TimeDataPreprocessing() { // 시간 차이 계산
		Date d1, d2;
		SimpleDateFormat dateFormat = new SimpleDateFormat("dHHmmss");
		for (int i = time.size() - 1; i > 0; i--) {
			try {
				d1 = dateFormat.parse(Long.toString(time.get(i)));
				d2 = dateFormat.parse(Long.toString(time.get(i - 1)));
				time.set(i, (long) ((d1.getTime() - d2.getTime()) / 1000));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		time.set(0, (long) 0);

	}

	public void warpingDistance(double D) { // DTW 실행
		int now, i;
		boolean pruning; // back tracking 가지치기 여부

		if (match < 6) { // 작으면 데이터 유효 X
			dtw[match - 1][match - 1] = 99999.9;
			return;
		}

		for (i = 1; i < match; i++) { // 첫번째 행, 열 warping distance
			dtw[i][0] = dtw[i - 1][0] + dtw[i][0];
			dtw[0][i] = dtw[0][i - 1] + dtw[0][i];
		}

		for (i = 1; i != match; i++) { // 총 warping distance 계산
			pruning = true;

			dtw[i][i] = min(i, i);
			if (dtw[i][i] < D)
				pruning = false;

			for (now = i + 1; now < match; now++) {// i번째 행과 열 계산
				dtw[i][now] = min(i, now);
				dtw[now][i] = min(now, i);
				if (dtw[now][i] < D | dtw[i][now] < D | pruning == false) { // 가지치기 여부
					pruning = false;
				}
			}

			if (pruning == true) { // true면 넘어감(가지치기 실행)
				dtw[match - 1][match - 1] = 99999.9;
				return;
			}
		}
	}

	public double distance(double x1, double x2, double y1, double y2) { // 두 좌표간 거리 계산
		return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
	}

	public double min(int a, int b) { // dtw 중간 연산
		return dtw[a][b] + Math.min(dtw[a - 1][b], Math.min(dtw[a][b - 1], dtw[a - 1][b - 1]));
	}

	public double result() { // 최종 dtw 거리 출력
		return dtw[match - 1][match - 1];
	}
}
