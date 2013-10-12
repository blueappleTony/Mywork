package controllers;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import jxl.Workbook;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import models.airport.StuInfo;

public class ExportStuinfo extends Application {

	public static void exportStu(String username) {
		try {
			//WritableWorkbook book = Workbook.createWorkbook(new File(
				//	"..//Desktop/接机学生信息.xls"));
			
			WritableWorkbook book = Workbook.createWorkbook(new File(
					"../接机学生信息.xls"));		
			
			WritableSheet sheet = book.createSheet("第一页", 0);

			jxl.write.Label label1 = new jxl.write.Label(0, 0, "姓名");
			sheet.addCell(label1);

			jxl.write.Label label2 = new jxl.write.Label(1, 0, "性别");
			sheet.addCell(label2);

			jxl.write.Label label3 = new jxl.write.Label(2, 0, "电话");
			sheet.addCell(label3);

			jxl.write.Label label4 = new jxl.write.Label(3, 0, "Email");
			sheet.addCell(label4);

			jxl.write.Label label5 = new jxl.write.Label(4, 0, "专业");
			sheet.addCell(label5);

			jxl.write.Label label6 = new jxl.write.Label(5, 0, "到达机场");
			sheet.addCell(label6);

			jxl.write.Label label7 = new jxl.write.Label(6, 0, "航班");
			sheet.addCell(label7);

			jxl.write.Label label8 = new jxl.write.Label(7, 0, "到达时间");
			sheet.addCell(label8);

			jxl.write.Label label9 = new jxl.write.Label(8, 0, "行李");
			sheet.addCell(label9);

			jxl.write.Label label10 = new jxl.write.Label(9, 0, "备忘");
			sheet.addCell(label10);
			
			jxl.write.Label label11 = new jxl.write.Label(10, 0, "学校");
			sheet.addCell(label11);

			 System.out.println("2222222222222222222222222");
			 System.out.println("看看传过来的:"+username);
			List<StuInfo> stu = StuInfo.find("SELECT a FROM StuInfo a WHERE school LIKE ?",
					"%" + username + "%").fetch();
			
			Iterator<StuInfo> iter = stu.iterator();
			StuInfo stuinformation;
			int k = 1;
			while (iter.hasNext()) {
				stuinformation = iter.next();
				sheet.addCell(new jxl.write.Label(0, k, stuinformation.name));
				sheet.addCell(new jxl.write.Label(1, k, stuinformation.airport));
				sheet.addCell(new jxl.write.Label(2, k, stuinformation.phone));
				sheet.addCell(new jxl.write.Label(3, k, stuinformation.email));
				sheet.addCell(new jxl.write.Label(4, k, stuinformation.major));
				sheet.addCell(new jxl.write.Label(5, k, stuinformation.airport));
				sheet.addCell(new jxl.write.Label(6, k, stuinformation.flight));
				sheet.addCell(new jxl.write.Label(7, k, stuinformation.date));
				sheet.addCell(new jxl.write.Label(8, k, stuinformation.luggage));
				sheet.addCell(new jxl.write.Label(9, k, stuinformation.remarks));
				sheet.addCell(new jxl.write.Label(10, k, stuinformation.school));
				k++;
			}

			book.write();
			book.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		// System.out.println("写入完成");
		render();
	}

}