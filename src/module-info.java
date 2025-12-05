module Lab6_SamitSandhu {
	requires javafx.controls;
	requires java.base;
	requires javafx.graphics;
	requires java.sql;
	
	opens application to javafx.graphics, javafx.fxml;
}
