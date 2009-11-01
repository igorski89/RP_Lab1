import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: igorevsukov
 * Date: Oct 27, 2009
 * Time: 12:55:15 AM
 * To change this template use File | Settings | File Templates.
 */
public class Sample {
    protected ArrayList<Double> originalSample = new ArrayList<Double>();

    public ArrayList<Double> getOriginalSample() { return originalSample; }

    protected ArrayList<Double> sample = new ArrayList<Double>();
    public ArrayList<Double> getSample() { return sample; }
    public double get(int i) { return sample.get(i); }
    protected void set(int i, double v) { sample.set(i, v); }
    public int size() { return sample.size(); }

    public Sample() {
    }

    public Sample(String filename) {
        this.loadFromFile(filename);

        removeAnomal();
        calcAutocorelationSimilarity();
        smoothByMedian();
        smoothByLS();
        calcS31();
        calcS42();
    }

    protected void loadFromFile(String filename) {
        try {
            BufferedReader input = new BufferedReader(new FileReader(filename));
            originalSample.clear();
            sample.clear();

            String line;
            while ((line = input.readLine()) != null) {
                originalSample.add(new Double(line));
                sample.add(new Double(line));
            }

            input.close();
        }
        catch (FileNotFoundException nofileex) {
            nofileex.printStackTrace();
        }
        catch (IOException ioex) {
            ioex.printStackTrace();
        }

    }

    public void removeAnomal() {
        //процедура 2.1
        //по старой методичке
        final int N = this.size();
//        final int k = 3;
//        double mean = 0;
//        for (int j = 0; j < N; j++) mean += this.get(j);
//        mean /= N;
//
//        double si = 0;
//        for (int j = 0; j < N; j++) si += Math.pow(this.get(j) - mean, 2.0);
//        si /= (N - 1);
//        si = Math.sqrt(si);
//        System.out.printf("mean=%f, si=%f\n",mean,si);
//        for (int i=1; i<N; i++) {
//            System.out.printf("%f < %f < %f\n",mean - k*si,this.get(i),mean + k*si);
//            if ( ( (mean - k*si) > this.get(i) ) || ( this.get(i) > (mean + k*si) ) ) {
//
//                this.set(i, 2*(this.get(i) - this.get(i-1)));
//            }
//        }
        //херная какая-то

        //Процедура 2.3
        double mean = 0;
        for (int j = 0; j < N; j++) mean += this.get(j);
        mean /= N;

        double si = 0;
        for (int j = 0; j < N; j++) si += Math.pow(this.get(j) - mean, 2.0);
        si /= (N - 1);
        si = Math.sqrt(si);
        double ta = Quantiles.anomalTNAlpha(this.size(),0.05);

        for(int i = 1; i < N - 1; i++)
            if(Math.abs((this.get(i) - mean) / si) >= ta)
                this.set(i, (this.get(i-1) + this.get(i+1)) / 2.0);

        if(Math.abs((this.get(N - 1) - mean) / si) >= ta)
            this.set(N - 1, this.get(N - 2));

        if(Math.abs((this.get(0) - mean) / si) >= ta)
            this.set(0, this.get(1));
    }

    
    protected double[][] autocorelationCoficients;
    public double[][] getAutocorelationCoficients() { return autocorelationCoficients; }
    protected void calcAutocorelationCoficients() {
        int K = this.size() / 3;
        int L = this.size() / 3;
//        autocorelationCoficients = new double[K+1][L];
        autocorelationCoficients = new double[L][K+1];

         for (int tau=1; tau<=L; tau++) {
            for (int k=0; k<=K; k++) {
                double kTau = 0;
                int N = size() - k;
                double m1 = 0, m2 = 0;
                double d1 = 0, d2 = 0;
                for(int i = 0; i < (N - tau); i++){
                    m1 += get(i);
                    m2 += get(i+tau);
                }
                m1 /= (N - tau);
                m2 /= (N - tau);
                for(int i = 0; i < (N - tau); i++){
                    d1 += (get(i) - m1) * (get(i) - m1);
                    d2 += (get(i+tau) - m2) * (get(i+tau) - m2);
                }
                d1 /= (N - tau - 1);
                d2 /= (N - tau - 1);
                for(int i = 0; i < (N- tau); i++)
                    kTau += (get(i) - m1) * (get(i+tau) - m2);
                kTau /= (double)(N - tau); 
                autocorelationCoficients[tau-1][k]= kTau / Math.sqrt(d1 * d2);
            }
        }            
    }

    protected boolean[] autocorelationSimilarities;
    public boolean[] getAutocorelationSimilarities() { return autocorelationSimilarities; }

    protected double[] autocorelationStatistics;
    public double[] getAutocorelationStatistics() { return autocorelationStatistics; }

    protected double[] autocorelationQuantiles;
    public double[] getAutocorelationQuantiles() { return autocorelationQuantiles; }

    public void calcAutocorelationSimilarity() {
        this.calcAutocorelationCoficients();
        final int N = this.size();
        final int K = N/3;
        final int L = N/3;
        autocorelationSimilarities = new boolean[L];
        autocorelationStatistics = new double[L];
        autocorelationQuantiles = new double[L];
        double quantile = Quantiles.hi2(0.05, K);

        for (int tau=0; tau<L; tau++) {
            autocorelationQuantiles[tau] = quantile;

            double p1 = 0;
            double p2 = 0;
            double p3 = 0;
            for (int k=0; k <=K; k++) {
                double ztk = 0.5*Math.log(1 + autocorelationCoficients[tau][k])/(1 - autocorelationCoficients[tau][k]);

                p1 += (N - k - 3) * Math.pow(ztk, 2);
                p2 += (N - k - 3) * ztk;
                p3 += (N - k - 3);
            }

            double statistic = p1 - Math.pow(p2, 2)/p3;
            autocorelationStatistics[tau] = statistic;
            autocorelationSimilarities[tau] = statistic <= quantile;
        }

    }

    public boolean isStationaryByAutocorelation() {
        for (boolean similar: autocorelationSimilarities) {
            if (!similar) {
                return false;
            }
        }

        return true;
    }


    protected int trendP = 0;
    public int getTrendP() { return trendP; }

    protected double trendZ = 0.0;
    public double getTrendZ() { return trendZ; }

    protected double trendRo = 0.0;
    public double getTrendRo() { return trendRo; }

    public boolean isHaveTrend() {
        final int N = this.size();
        final double alpha = 0.05;
        int p = 0;
        for(int i=0; i<N-1; i++) {
            for(int j=i+1; j<N; j++) {
                if (this.get(j) > this.get(i)) {
                    p++;
                }
            }
        }

        double ro = 4*p/(N*(N-1)) - 1;

        double z = (3*ro*Math.sqrt(N*(N-1)))/Math.sqrt(2*(2*N+5));

        trendP = p;
        trendRo = ro;
        trendZ = z;

        return !(Math.abs(z) < Quantiles.norm(alpha/2));
    }


    protected ArrayList<Point2D> medianSmoothedSample = new ArrayList<Point2D>();
    public ArrayList<Point2D> getMedianSmoothedSample() { return medianSmoothedSample; }

    public void smoothByMedian() {
        double a = 1.0/3.0;
        int N = this.size() + 2;
        Point2D[] M = new Point2D[N];
        Point2D[] M1 = new Point2D[N];

        for (int i=0; i<N; i++) {
            if (i == 0 || i == N - 1) {
                M[i] = new Point2D.Double();
            }
            else {
                M[i] = new Point2D.Double(i, this.get(i-1));
            }

            M1[i] = new Point2D.Double();
        }

        N = this.size();
        double L, L1, A;

        do {
            double M0x = (4*M[1].getX() + M[2].getX() - 2*M[3].getX()) / 3.0;
            double M0y = (4*M[1].getY() + M[2].getY() - 2*M[3].getY()) / 3.0;
            M[0].setLocation(M0x, M0y);

            double MN1x = (4*M[N].getX() + M[N-1].getX() - 2*M[N-2].getX()) / 3.0;
            double MN1y = (4*M[N].getY() + M[N-1].getY() - 2*M[N-2].getY()) / 3.0;
            M[N+1].setLocation(MN1x,MN1y);

            for (int i=1; i <= N; i++) {
                double M1x = M[i].getX() + a*(M[i-1].getX() - 2*M[i].getX() + M[i].getX());
                double M1y = M[i].getY() + a*(M[i-1].getY() - 2*M[i].getY() + M[i].getY());
                M1[i].setLocation(M1x, M1y);
            }

            L = L1 = A = 0;
            for (int i=2; i <= N; i++) {
                L+= M[i].distance(M[i-1]);
                L1 += M1[i].distance(M1[i-1]);

                double qti = M[i].getX() - M[i-1].getX();
                double pti = (M[i+1].getX() - M[i].getX()) - 2*qti + (M[i-1].getX() - M[i-2].getX());

                double qxi = M[i].getY() - M[i-1].getY();
                double pxi = (M[i+1].getY() - M[i].getY()) - 2*qxi + (M[i-1].getY() - M[i-2].getY());

                double Ti = (qti*pti - qxi*pxi)/(2*M1[i].distance(M1[i-1]));
                A += M1[i].distance(M1[i-1])*Ti;
            }


            for (int i=0; i<M1.length; i++) {
                M[i].setLocation(M1[i]);
            }
        } while (!(Math.abs(L-L1) <= A*a*Math.pow(L1/N,2)));

        medianSmoothedSample.clear();
        for (int i=2; i<M.length - 2; i++) {
//            medianSmoothedSample.add(M[i]);
            medianSmoothedSample.add(new Point2D.Double(M[i].getX()-1,M[i].getY()));
        }
        medianSmoothedSample.add(0, new Point2D.Double(0,sample.get(0)));
        medianSmoothedSample.add(new Point2D.Double(size()-1,sample.get(size()-1)));
    }


    protected ArrayList<Double> lsSmoothedSample = new ArrayList<Double>();
    public ArrayList<Double> getLsSmoothedSample() { return lsSmoothedSample; }

    public void smoothByLS() {
        final int N = this.size();
        lsSmoothedSample.clear();

        int i = 3;
        double a0 = (-2*get(i-3) +3*get(i-2) +6*get(i-1) +7*get(i) +6*get(i+1) +3*get(i+2) -2*get(i+3))/20;
        // a1
        double txit = 0;
        double t3xit = 0;
        for (int t = -3; t <= 3; t++) {
            txit += t * get(i + t);
            t3xit += t * t * t * get(i + t);
        }
        double a1 = (397 * txit - 49 * t3xit) / 1512.0;
        // a2
        double xit = 0;
        double t2xit = 0;
        for (int t = -3; t <= 3; t++) {
            xit += get(i + t);
            t2xit += t * t * get(i + t);
        }
        double a2 = (-4 * xit + t2xit) / 84.0;
        //a3
//            double txt = 0;
        //t^3*x(i+t) у нас уже есть
//        for (int t=-3; t <= 3; t++) {
//            txt += t*get(t); //а вот тут херня какая то, у меня же нет x(-3)
//        }
        double a3 = (-7 * txit + t3xit) / 216.0;

        for (int t = -3; t <= 0; t++) { lsSmoothedSample.add(a0 + a1*t + a2*t*t + a3*t*t*t); }

        for (i=4; i < N-4; i++) {
            lsSmoothedSample.add((-2*get(i-3) +3*get(i-2) +6*get(i-1) +7*get(i) +6*get(i+1) +3*get(i+2) -2*get(i+3))/20.0);
        }
        
        i = N-4;
        a0 = (-2*get(i-3) +3*get(i-2) +6*get(i-1) +7*get(i) +6*get(i+1) +3*get(i+2) -2*get(i+3))/20;
        // a1
        txit = 0;
        t3xit = 0;
        for (int t = -3; t <= 3; t++) {
            txit += t * get(i + t);
            t3xit += t * t * t * get(i + t);
        }
        a1 = (397 * txit - 49 * t3xit) / 1512.0;
        // a2
        xit = 0;
        t2xit = 0;
        for (int t = -3; t <= 3; t++) {
            xit += get(i + t);
            t2xit += t * t * get(i + t);
        }
        a2 = (-4 * xit + t2xit) / 84.0;
        //a3
//            double txt = 0;
        //t^3*x(i+t) у нас уже есть
//            for (int t=-3; t <= 3; t++) {
//                txt += t*get(t); //а вот тут херня какая то, у меня же нет x(-3)
//            }
        a3 = (-7 * txit + t3xit) / 216.0;

        for (int t = 0; t < 4; t++) { lsSmoothedSample.add(a0 + a1*t + a2*t*t + a3*t*t*t); }

    }

    protected ArrayList<Point2D> s31 = new ArrayList<Point2D>();
    public ArrayList<Point2D> getS31() { return s31; }
    public void calcS31() {
        s31.clear();
        double x = 3;
        double dx = 0.1;
        final int N = this.size();
        double h  = 1;
        s31.add(new Point2D.Double(0,get(0)));
        s31.add(new Point2D.Double(1,get(1)));
        s31.add(new Point2D.Double(2,get(2)));
        while(x <= N-4){
//            int ind = (int)Math.round(x);
            int ind = (int)x;

            double y = (2.0 / h) * (x - (ind + 0.5)*h);
//            System.out.printf("x=%3.2f\tind=%d\ty=%3.4f\n",x,ind,y);
            double p_3,p_2,p_1,p,p1,p2,p3;
            p_3 = get(ind - 3);
            p_2 = get(ind - 2);
            p_1 = get(ind - 1);
            p = get(ind);
            p1 = get(ind+1);
            p2 = get(ind+2);
            p3 = get(ind+3);
            double res;
            res = (1.0/1152.0)*(5*p_2 -49*p_1 +122*p -122*p1 +49*p2 -5*p3)*y*y*y;
            res += (1.0/384.0)*(-5*p_2 +39*p_1 -34*p -34*p1 +39*p2 -5*p3)*y*y;
            res += (1.0/384.0)*(5*p_2 -9*p_1 -190*p +190*p1 +9*p2 -5*p3)*y;
            res += (1.0/1152.0)*(-5*p_2 -81*p_1 +662*p +662*p1 -81*p2 -5*p3);

            s31.add(new Point2D.Double(x,res));

            x += dx;
        }
        s31.add(new Point2D.Double(N-3,get(N-3)));
        s31.add(new Point2D.Double(N-2,get(N-2)));
        s31.add(new Point2D.Double(N-1,get(N-1)));
    }

    protected ArrayList<Point2D> s42 = new ArrayList<Point2D>();
    public ArrayList<Point2D> getS42() { return s42; }
    public void calcS42() {
        s42.clear();
        double x = 4;
        double dx = 0.1;
        final int N = this.size();
        double h  = 1;
        s42.add(new Point2D.Double(0,get(0)));
        s42.add(new Point2D.Double(1,get(1)));
        s42.add(new Point2D.Double(2,get(2)));
        while(x <= N-5){
            int ind = (int)Math.round(x);

            double y = 2.0 * (x - ind * h) / h;
            double p_4,p_3,p_2,p_1,p,p1,p2,p3,p4;
//            System.out.printf("x=%3.2f\tind=%d\ty=%3.4f\n",x,ind,y);
            p_4 = get(ind - 4);
            p_3 = get(ind - 3);
            p_2 = get(ind - 2);
            p_1 = get(ind - 1);
            p = get(ind);
            p1 = get(ind+1);
            p2 = get(ind+2);
            p3 = get(ind+3);
            p4 = get(ind+4);
            double res;
            
            res = (13*p_4 -164*p_3 +964*p_2 -2588*p_1 +3550*p -2588*p1 +964*p2 -164*p3 +13*p4)*y*y*y*y;
            res += (-52*p_4 +552*p_3 -2648*p_2 +3848*p_1 -3848*p1 +2648*p2 -552*p3 +52*p4)*y*y*y;
            res += (78*p_4 -360*p_3 -840*p_2 +16872*p_1 -31500*p +16872*p1 -840*p2 -360*p3 +78*p4)*y*y;
            res += (-52*p_4 -696*p_3 +8103*p_2 -36952*p_1 +36952*p1 -8104*p2 +696*p3 +52*p4)*y;
            res += 13*p_4 +876*p_3 -5084*p_2 +8404*p_1 + 83742*p + 8404*p1 -5084*p2 +876*p3 +13*p4;

            res /= 92160.0;

            s42.add(new Point2D.Double(x,res));

            x += dx;
        }
        s42.add(new Point2D.Double(N-3,get(N-3)));
        s42.add(new Point2D.Double(N-2,get(N-2)));
        s42.add(new Point2D.Double(N-1,get(N-1)));
    }
}
