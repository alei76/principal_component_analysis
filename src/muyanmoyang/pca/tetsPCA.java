package muyanmoyang.pca;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import muyanmoyang.loadData.DataLoad;
import Jama.Matrix;

public class tetsPCA {
	public static void main(String[] args) throws IOException {
//		DataLoad data = new DataLoad() ;
//		PCA pca = new PCA() ;
//		// ����ѵ������
//		Map<String,List<Double>> trainMap = data.loadTrainData("C:/Users/Administrator/Desktop/DataCastle/test_x.csv") ;
//		// ����ѵ����������е�ƽ��ֵ
//		List<Double> averageList = pca.computeDataAdjust(trainMap,"C:/Users/Administrator/Desktop/DataCastle/���Լ�/�������ݵ�ƽ��ֵ.csv") ;
//		// �����������Ļ���ľ���B
//		Map<String,List<Double>> map_B = pca.computeB(trainMap, averageList,"C:/Users/Administrator/Desktop/DataCastle/���Լ�/B_matrix.csv") ; // �õ��������Ļ���B����
//		// ����Э�������
//		Double[][] matrix_B = pca.getMatrixFromB(map_B) ;
//		double[][] covariance_matrix = pca.computeCovariance(matrix_B) ;
//		// ��������ֵ
//		double[][] eigenvalue_matrix = pca.getEigenvalueMatrix(covariance_matrix) ;
//		// ������������
//		double[][] eigenvector_matrix = pca.getEigenVectorMatrix(covariance_matrix) ;
//		
//		// ���ɷ־���
//		Matrix principalMatrix = pca.getPrincipalComponent(eigenvalue_matrix, eigenvector_matrix);
//		// ��ά��ľ���
//		Matrix resultMatrix = pca.getResult(trainMap, principalMatrix);
		
//		dataProcess process = new dataProcess() ;
//		process.processNewMatrix("C:/Users/Administrator/Desktop/DataCastle/���Լ�/��ά����.csv",
//					"C:/Users/Administrator/Desktop/DataCastle/���Լ�/new_��ά����.csv");
	}
}
