package webapp;

import java.io.IOException;

import org.json.JSONArray;

import com.ruby.framework.controller.ControllerBase;

public class Default extends ControllerBase {
	public void index() throws IOException{
		String result = _model.sqlite_model.executeQuery("select * from students");
		JSONArray array = new JSONArray(result);
		String student_name = "";
		if (array.length() >= 1) {
			student_name = array.getJSONObject(0).getString("name");
		}
		assign("student_name", student_name);
		display("index",1);
	}
}
