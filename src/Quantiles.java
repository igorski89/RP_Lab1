
public class Quantiles {
    /**
	 * @param nu ню //искренне Ваш, К.О.
	 * @param a помилка першого роду
	 * @return квантиль Стьюдента
	 */
	public static double Student(double nu, double a){
		double quantNorm = norm(a);

		double g1 = (double) (
				Math.pow(quantNorm, 3)
				+ quantNorm
		) / 4.0;

		double g2 = (double) (

				5 * Math.pow(quantNorm, 5)
				+ 16 * Math.pow(quantNorm, 3)
				+ 3 * quantNorm

		) / 96.0;

		double g3 = (double) (

				3 * Math.pow(quantNorm, 7)
				+ 19 * Math.pow(quantNorm, 5)
				+ 17 * Math.pow(quantNorm, 3)
				- 15 * quantNorm

		) / 384.0;

		double g4 = (double) (

				79 * Math.pow(quantNorm, 9)
				+ 779 * Math.pow(quantNorm, 7)
				+ 1482 * Math.pow(quantNorm, 5)
				- 1920 * Math.pow(quantNorm, 3)
				- 945 * quantNorm

		) / 92160.0;

		double quant = quantNorm
		+ g1 / nu
		+ g2 / Math.pow(nu, 2)
		+ g3 / Math.pow(nu, 3)
		+ g4 / Math.pow(nu, 4);

		return quant;
	}


    /**
     * @param a помилка першого роду
     * @return квантиль нормальго розподілу
     */
    public static double norm(double a){

        double c0 = 2.515517;
        double c1 = 0.802853;
        double  c2 = 0.010328;
        double  d1 = 1.432788;
        double  d2 = 0.1892659;
        double  d3 = 0.001308;
        double  e = 0;//4.5e-4;
        double p = a;
        double t = Math.sqrt((-1)*Math.log(p*p));

        return t - (c0+c1*t+c2*t*t) /(1+d1*t+d2*t*t+d3*t*t*t)+e;
    }

    /** 
     * @param N кількість елементів вибірки
     * @param alpha помилка першого роду
     * @return квантиль для видалення аномальних значень в процедурі 2.3
     */
    public static double anomalTNAlpha(int N, double alpha) {
        //считаем что aplha == 0.05, иначе - я не играю
        //ну и считать оно будет пол года
        if (N <= 50) {
            double a = -0.000347;
            double b = 0.312449;
            double c = 1.072398;
            return (double) N / (a * N * N + b * N + c);
        }
        else {
            double t = 2.0 * (N - 1);
            double u = 3.9;
            t /= (2 * N - 5 + u * u) + (3 + u * u + 2 * u * u * u * u) / (12 * N - 30);
            t = u * Math.sqrt(t);
            return t;
        }
    }

    public static double hi2(double a,double nu) {
        double skobka = 1 - (double) 2/(9*nu) + norm(a)* Math.sqrt( (double)2/(9*nu) );
        double hiQuant = nu * Math.pow(skobka, 3);

        return hiQuant;
    }
}
