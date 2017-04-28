function schedule_posts_calendar_quick_schedule_cancel(id) {
	var table = document.getElementById("mytable");
	for (i = 0; i < table.rows.length; i++) {
		if (table.rows[i].id == "myid") {
			table.deleteRow(i);
			i = table.rows.length;
		}
	}
}