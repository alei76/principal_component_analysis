package muyanmoyang.pca;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;

import Jama.Matrix;
import muyanmoyang.loadData.DataLoad;

/*
 *  ���ɷַ���
 */
public class PCA {
	
	public static void main(String[] args) throws IOException { 
		DataLoad data = new DataLoad() ;
		PCA pca = new PCA() ;
		// ����ѵ������
		Map<String,List<Double>> trainMap = data.loadTrainData("C:/Users/Administrator/Desktop/DataCastle/����TFIDF2.txt") ;
		// ����ѵ����������е�ƽ��ֵ
		List<Double> averageList = pca.computeDataAdjust(trainMap,"C:/Users/Administrator/Desktop/DataCastle/�������ݵ�ƽ��ֵ.csv") ;
		// �����������Ļ���ľ���B
		Map<String,List<Double>> map_B = computeB(trainMap, averageList,"C:/Users/Administrator/Desktop/DataCastle/B_matrix.csv") ; // �õ��������Ļ���B����
		// ����Э�������
		Double[][] matrix_B = getMatrixFromB(map_B) ;
		double[][] covariance_matrix = computeCovariance(matrix_B) ;
		// ��������ֵ
		double[][] eigenvalue_matrix = getEigenvalueMatrix(covariance_matrix) ;
		// ������������
		double[][] eigenvector_matrix = getEigenVectorMatrix(covariance_matrix) ;
		
		// ���ɷ־���
		Matrix principalMatrix = pca.getPrincipalComponent(eigenvalue_matrix, eigenvector_matrix);
		// ��ά��ľ���
		Matrix resultMatrix = pca.getResult(trainMap, principalMatrix);
		
		
	}
	private static final double threshold = 0.9999 ;// ����ֵ��ֵ
	/*
	 * ����ѵ������ÿ��ƽ��ֵ
	 */
	static List<Double> computeDataAdjust(Map<String,List<Double>> trainMap,String averageWriter) throws IOException{
		Map<Integer,List<Double>> tmp = new LinkedHashMap<Integer,List<Double>>() ;
		FileWriter writer = new FileWriter(new File(averageWriter)) ;
		List<Double> averageList = new ArrayList<Double>() ;
		Double average ;
		Set<Entry<String,List<Double>>> set = trainMap.entrySet() ;
		Iterator<Entry<String,List<Double>>> it = set.iterator() ;
		int count = 0 ;
		while(it.hasNext()){
			count ++ ;
			System.out.println(count) ;
			Entry<String,List<Double>> entry = it.next() ;
			List<Double> list = entry.getValue() ;
			tmp.put(count,list) ;
			System.gc();
		}
		for(int i=0; i<1000; i++){
			System.out.println("�������ƽ��ֵ��" + (i+1));  
			List<Double> column = new ArrayList<Double>() ;
			Iterator<Entry<Integer,List<Double>>> it2 = tmp.entrySet().iterator() ;
//			System.out.println("it2:" + it2.next().getValue());
			while(it2.hasNext()){
				Entry<Integer, List<Double>> entry2 = it2.next() ;
				System.out.println("-----" + entry2.getValue().size()) ;
				column.add(entry2.getValue().get(i)) ;
			}
			average = computeAverage(column) ;
			averageList.add(average) ;
		}
		int columnNum = 0 ;
		for(Double num : averageList){
			columnNum ++ ;
			writer.write(columnNum + "\t" + num + "\n");
			writer.flush();
		}
		writer.close();
		return averageList ;
	}

	private static Double computeAverage(List<Double> column) { 
		// TODO Auto-generated method stub
		Double sum = 0.0 ; 
		for(int i=0; i<column.size(); i++){
			sum += column.get(i) ;
		}
		return sum / column.size() ;
	}
	
	/*
	 *  �����������Ļ���ľ���B�����м�ȥ���о�ֵ
	 */
	static Map<String,List<Double>> computeB(Map<String,List<Double>> trainMap,List<Double> averageList,String B_matrixWriter) throws IOException{
		FileWriter BmatrixWriter = new FileWriter(new File(B_matrixWriter)) ;
		Set<Entry<String,List<Double>>> set = trainMap.entrySet() ;
		Iterator<Entry<String,List<Double>>> it = set.iterator() ;
		Map<String,List<Double>> new_trainMap = new LinkedHashMap<String,List<Double>>() ;		
		
		Double average ;
		int count = 0 ;
		while(it.hasNext()){
			count ++ ;
			System.out.println("�������Ļ�����" + count) ;
			List<Double> newList = new ArrayList<Double>() ;
			Entry<String,List<Double>> entry = it.next() ;
			List<Double> list = entry.getValue() ;
			String users = entry.getKey() ;
			for(int i=0; i<averageList.size(); i++){
				newList.add(list.get(i)-averageList.get(i)) ;
			}
			new_trainMap.put(users, newList) ;
			System.gc();
		}
		
		Iterator<Entry<String,List<Double>>> it2 = new_trainMap.entrySet().iterator() ;
		while(it2.hasNext()){
			Entry<String,List<Double>> entry = it2.next() ;
			List<Double> list = entry.getValue() ;
			BmatrixWriter.write(entry.getKey() + "\t");
			for(Double elem : list){
				BmatrixWriter.write(elem + "\t");
			}
			BmatrixWriter.write("\n");
			BmatrixWriter.flush();
		}
		BmatrixWriter.close();
		return new_trainMap ;
	}
	
	static Double[][] getMatrixFromB(Map<String,List<Double>> matrix_B){
		int row = matrix_B.entrySet().iterator().next().getValue().size() ; // �����������������
		int column = matrix_B.size() ; // ����
		Double[][] B_Matrix = new Double[column][row]; 
		
		Iterator<Entry<String, List<Double>>> it = matrix_B.entrySet().iterator();
		int count = 0 ;
		while(it.hasNext()){
			Entry<String, List<Double>> entry = it.next() ;
			List<Double> list = entry.getValue() ;
			for(int i=0; i<list.size(); i++){
				B_Matrix[count][i] = list.get(i) ;
			}
			count ++ ;
			System.out.println("�������Ļ�����map����>����" + count);
		}
 		return B_Matrix ;
	}
	
	/*
	 *  ����Э�������
	 */
	static double[][] computeCovariance(Double[][] matrix_B) throws IOException{ 
		java.text.DecimalFormat df = new java.text.DecimalFormat("#.0000"); 
		int column = matrix_B[0].length ; // �����������������
		System.out.println("������" + column) ;
		int row = matrix_B.length ; // ����
		System.out.println("������" + row) ;
		double[][] covariance_Matrix = new double[column][column];
		for(int i=0; i<column; i++){ // ��
			System.out.println("����Э�������" + (i+1)) ;
			for(int j=0; j<column; j++){ // ��
				Double sum = 0.0 ;
				for(int k=0; k< row; k++){
					sum += matrix_B[k][i] * matrix_B[k][j] ;
				}
				covariance_Matrix[i][j] =  Double.parseDouble(df.format((sum / (row-1)))) ;  //   (column * column)ά��
			}
		}
		
		printMatrix("C:/Users/Administrator/Desktop/DataCastle/���Լ�/Э�������.csv",covariance_Matrix);
		return covariance_Matrix ;
	}

	
	/**
	 * ������ֵ����
	 * 
	 * @param covariance_Matrix			Э�������
	 * @return result 					����������ֵ��ά�������
	 * @throws IOException 
	 */
	public static double[][] getEigenvalueMatrix(double[][] covariance_Matrix) throws IOException {
		Matrix A = new Matrix(covariance_Matrix);
		// ������ֵ��ɵĶԽǾ���,eig()��ȡ����ֵ
//		A.eig().getD().print(10, 6);
		System.out.println("��������ֵ...") ;
		double[][] eigenvalue_matrix = A.eig().getD().getArray();
		printMatrix("C:/Users/Administrator/Desktop/DataCastle/���Լ�/����ֵ����.csv", eigenvalue_matrix); 
		return eigenvalue_matrix;
	}
	
	/**
	 * ��׼������������������
	 * 
	 * @param eigenvalue_matrix			����ֵ����
	 * @return result 			��׼����Ķ�ά�������
	 * @throws IOException 
	 */
	public static double[][] getEigenVectorMatrix(double[][] covariance_Matrix) throws IOException {
		System.out.println("������������...") ;
		Matrix A = new Matrix(covariance_Matrix);
//		A.eig().getV().print(6, 2);
		double[][] eigenvector_matrix = A.eig().getV().getArray();
		printMatrix("C:/Users/Administrator/Desktop/DataCastle/���Լ�/������������.csv", eigenvector_matrix); 
		return eigenvector_matrix;
	}
	
	/**
	 * Ѱ�����ɷ�,ѡȡ�������ֵ��Ӧ�������������õ��µ����ݼ�
	 * 
	 * @param prinmaryArray		ԭʼ��ά��������
	 * @param eigenvalue		����ֵ��ά����
	 * @param eigenVectors		����������ά����
	 * @return principalMatrix 	���ɷ־���
	 */
	public Matrix getPrincipalComponent(double[][] eigenvalue, double[][] eigenVectors){
		System.out.println("ѡȡ���ɷ�...") ;
		Matrix A = new Matrix(eigenVectors);// ����һ��������������
		double[][] tEigenVectors = A.transpose().getArray();// ��������ת��
		Map<Integer, double[]> principalMap = new HashMap<Integer, double[]>();// key=���ɷ�����ֵ��value=������ֵ��Ӧ����������
		TreeMap<Double, double[]> eigenMap = new TreeMap<Double, double[]>(
				Collections.reverseOrder());// key=����ֵ��value=��Ӧ��������������ʼ��Ϊ��ת����ʹmap��keyֵ��������
		double total = 0;// �洢����ֵ�ܺ�
		int index = 0, n = eigenvalue.length;
		double[] eigenvalueArray = new double[n];// ������ֵ����Խ����ϵ�Ԫ�طŵ�����eigenvalueArray��
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				if (i == j)
					eigenvalueArray[index] = eigenvalue[i][j];
			}
			index++;
		}

		for (int i = 0; i < tEigenVectors.length; i++) {
			double[] value = new double[tEigenVectors[0].length];
			value = tEigenVectors[i];
			eigenMap.put(eigenvalueArray[i], value);
		}

		// �������ܺ�
		for (int i = 0; i < n; i++) {
			total += eigenvalueArray[i];
		}
		// ѡ��ǰ�������ɷ�
		double temp = 0;
		int principalComponentNum = 0;// ���ɷ���
		List<Double> plist = new ArrayList<Double>();// ���ɷ�����ֵ
		for (double key : eigenMap.keySet()) {
			if (temp / total <= threshold) {
				temp += key;
				plist.add(key);
				principalComponentNum++;
			}
		}
		System.out.println("\n" + "��ǰ��ֵ: " + threshold);
		System.out.println("ȡ�õ����ɷ���: " + principalComponentNum + "\n");

		// �����ɷ�map����������
		for (int i = 0; i < plist.size(); i++) {
			if (eigenMap.containsKey(plist.get(i))) {
				principalMap.put(i, eigenMap.get(plist.get(i)));
			}
		}

		// ��map���ֵ�浽��ά������
		double[][] principalArray = new double[principalMap.size()][];
		Iterator<Entry<Integer, double[]>> it = principalMap.entrySet()
				.iterator();
		for (int i = 0; it.hasNext(); i++) {
			principalArray[i] = it.next().getValue();
		}

		Matrix principalMatrix = new Matrix(principalArray);
		System.out.println("���ɷ־���ά��:" + principalMatrix.getRowDimension() + "X" + principalMatrix.getColumnDimension());
		return principalMatrix;
	}

	/**
	 * �������
	 * @param primary		ԭʼ��ά����
	 * @param matrix		���ɷ־���
	 * @return result 		�������
	 * @throws IOException 
	 */
	public Matrix getResult(Map<String,List<Double>> trainMap, Matrix matrix) throws IOException {
		PrintWriter resultWriter = new PrintWriter(new FileWriter(new File
								("C:/Users/Administrator/Desktop/DataCastle/���Լ�/��ά����.csv"))) ;
		int column = trainMap.entrySet().iterator().next().getValue().size() ; // �����������������
		int row = trainMap.size() ; // ����
		double[][] train_Matrix = new double[row][column];
		
		Iterator<Entry<String, List<Double>>> it = trainMap.entrySet().iterator();
		int count = 0 ;
		while(it.hasNext()){
			Entry<String, List<Double>> entry = it.next() ;
			List<Double> list = entry.getValue() ;
			for(int i=0; i<list.size(); i++){
				train_Matrix[count][i] = list.get(i) ;
			}
			count ++ ;
		}
		printMatrix("C:/Users/Administrator/Desktop/DataCastle/���Լ�/ԭʼ����.csv", train_Matrix); 
		
		
		Matrix primaryMatrix = new Matrix(train_Matrix);
		System.out.println("ά�ȣ�" + matrix.getRowDimension() + " * " + matrix.getColumnDimension());
//		matrix.print(matrix.getRowDimension(),  matrix.getColumnDimension()) ;
		Matrix result = primaryMatrix.times(matrix.transpose());
		
		int row2 = result.getRowDimension() ;
		int column2 = result.getColumnDimension() ;
		result.print(resultWriter, row2, column2);  // ����ά����¾���д���ļ�
		resultWriter.flush();
		resultWriter.close();
		return result;
	}
	
	/*
	 * 	��ӡ����
	 */
	private static void printMatrix(String writerDir,double[][] matrix) throws IOException {
		FileWriter writer = new FileWriter(new File(writerDir)) ;
		int row = matrix.length ;  //��
		System.out.println("������" + row + "��...");
		int column = matrix[0].length ; // ��
		System.out.println("������" + column + "��...");
		for(int i=0; i<row; i++){
			for(int j=0; j<column; j++){
				writer.write(matrix[i][j] + "\t") ;
			}
			writer.write("\n") ;
			writer.flush();
		}
		writer.close();
	}
}
