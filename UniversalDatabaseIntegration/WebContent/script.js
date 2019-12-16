var onImages  = new Array();
var offImages = new Array();

function putOn(id,index) {
	id.src = onImages[index].src;
}

function putOff(id,index) {
	id.src = offImages[index].src;
}

function loadImages() {
	var i;

	for(i=1;i<=9;i++) {
		onImages[i]  = new Image(80,26);
		offImages[i] = new Image(80,26);
	}
	
	for(i=10;i<=12;i++) {
		onImages[i]  = new Image(70,24);
		offImages[i] = new Image(70,24);
	}
	
	onImages[13]  = new Image(30,30);
	offImages[13] = new Image(30,30);
	
	for(i=14;i<=24;i++) {
		onImages[i]  = new Image(70,24);
		offImages[i] = new Image(70,24);
	}

	onImages[1].src	 = 'pics/structure2.jpg';
	onImages[2].src  = 'pics/properties2.jpg';
	onImages[3].src  = 'pics/browse2.jpg';
	onImages[4].src  = 'pics/sql2.jpg';
	onImages[5].src  = 'pics/import2.jpg';
	onImages[6].src  = 'pics/insert2.jpg';
	onImages[7].src  = 'pics/export2.jpg';
	onImages[8].src  = 'pics/operations2.jpg';
	onImages[9].src  = 'pics/search2.jpg';
	onImages[10].src = 'pics/login2.jpg';
	onImages[11].src = 'pics/reset2.jpg';
	onImages[12].src = 'pics/fetch2.jpg';
	onImages[13].src = 'pics/go2.jpg';
	onImages[14].src = 'pics/add2.jpg';
	onImages[15].src = 'pics/change2.jpg';
	onImages[16].src = 'pics/remove2.jpg';
	onImages[17].src = 'pics/create2.jpg';
	onImages[18].src = 'pics/apply2.jpg';
	onImages[19].src = 'pics/drop2.jpg';
	onImages[20].src = 'pics/empty2.jpg';
	onImages[21].src = 'pics/run2.jpg';
	onImages[22].src = 'pics/export12.jpg';
	onImages[23].src = 'pics/import12.jpg';
	onImages[24].src = 'pics/search12.jpg';

	offImages[1].src  = 'pics/structure1.jpg';
	offImages[2].src  = 'pics/properties1.jpg';
	offImages[3].src  = 'pics/browse1.jpg';
	offImages[4].src  = 'pics/sql1.jpg';
	offImages[5].src  = 'pics/import1.jpg';
	offImages[6].src  = 'pics/insert1.jpg';
	offImages[7].src  = 'pics/export1.jpg';
	offImages[8].src  = 'pics/operations1.jpg';
	offImages[9].src  = 'pics/search1.jpg';
	offImages[10].src = 'pics/login1.jpg';
	offImages[11].src = 'pics/reset1.jpg';
	offImages[12].src = 'pics/fetch1.jpg';
	offImages[13].src = 'pics/go1.jpg';
	offImages[14].src = 'pics/add1.jpg';
	offImages[15].src = 'pics/change1.jpg';
	offImages[16].src = 'pics/remove1.jpg';
	offImages[17].src = 'pics/create1.jpg';
	offImages[18].src = 'pics/apply1.jpg';
	offImages[19].src = 'pics/drop1.jpg';
	offImages[20].src = 'pics/empty1.jpg';
	offImages[21].src = 'pics/run1.jpg';
	offImages[22].src = 'pics/export11.jpg';
	offImages[23].src = 'pics/import11.jpg';
	offImages[24].src = 'pics/search11.jpg';
}

function isEmpty(id) {
	var data = id.value;
	if(data == null || data == '' || data == " ") {
		alert(id.name + " must be entered.");
		id.focus();
		return true;
	}
	return false;
}

function isNumber(id) {
	var data = id.value;
	var len = data.length;
	var cmlen = 0;
	if(len > 0) {
		for(var i=0;i<len;i++)	{
			var ch = data.charAt(i);
			if(i == 0 || cmlen > 0) {
				if(ch >= 0 && ch <= 9) continue;
				else return -1;
			}
			else if(ch == ',') cmlen = 1;
			else if(ch >= 0 && ch <= 9) continue;
			else return -1;
		}
	}
	else return 0;
	return data;
}

function isExists(id,val) {
	var len = id.options.length;
	if(len > 0) {
		for(var i=0;i<len;i++) {
			var lval = id.options[i].value;
			if(lval == val) return true;
		}
	}
	else return false;
	return false;
}

function addField(theForm)	{
	var colName = theForm.field_name.value;
	var colType = theForm.field_type.value;
	var colSize = isNumber(theForm.field_size);
	var primary = (theForm.primary.checked) ? 1 : 0;
	var unique  = (theForm.unique.checked) ? 1 : 0;
	var notnull = (theForm.notnull.checked) ? 1 : 0;

	if(isEmpty(theForm.field_name));
	else if(isExists(theForm.column_names,colName)) {
		alert("Field Name already Exists. Please enter another name.");
		theForm.field_name.focus();
	}
	else if( colSize == -1) {
		alert("Field size must be a valid number");
		theForm.field_size.focus();
	}
	else {
		var len = theForm.column_names.options.length;
		theForm.column_names.options[len] = new Option(colName);
		theForm.column_names.options[len].value = colName;
		theForm.column_types.options[len] = new Option(colType);
		theForm.column_types.options[len].value = colType;
		theForm.column_sizes.options[len] = new Option(colSize);
		theForm.column_sizes.options[len].value = colSize;
		theForm.column_pk.options[len] = new Option(primary);
		theForm.column_pk.options[len].value = primary;
		theForm.column_un.options[len] = new Option(unique);
		theForm.column_un.options[len].value = unique;
		theForm.column_nn.options[len] = new Option(notnull);
		theForm.column_nn.options[len].value = notnull;
	}
}

function loadValues(theForm,id) {
	var index = id.options.selectedIndex;
	var name  = id.options[index].value;

	if(index >= 0) {
		if(theForm == document.alter_form) {
			theForm.field_name.readOnly = false;
			var len = document.alter_form.prev_column_names.options.length;
			for(var i=0;i<len;i++) {
				var prevName  = document.alter_form.prev_column_names.options[i].value;
				var prevIndex = document.alter_form.prev_column_index.options[i].value;
				if(name == prevName && index == prevIndex) {
					theForm.field_name.readOnly = true;
					break;
				}
			}
		}
		theForm.field_name.value = name;
		theForm.field_type.value = theForm.column_types.options[index].value;
		theForm.field_size.value = theForm.column_sizes.options[index].value;

		theForm.primary.checked = (theForm.column_pk.options[index].value == 1) ? true : false;
		theForm.unique.checked  = (theForm.column_un.options[index].value == 1) ? true : false;
		theForm.notnull.checked = (theForm.column_nn.options[index].value == 1) ? true : false;
	}
}

function recordPrevAction(theForm,colname,index,action) {
	var len = theForm.prev_column_names.options.length;

	for(var i=0;i<len;i++) {
		var prevName  = theForm.prev_column_names.options[i].value;
		var prevIndex = theForm.prev_column_index.options[i].value;
		if(colname == prevName && index == prevIndex) {
			theForm.prev_column_action.options[i].value = action;
			if(action == -1) {
				var nextIndex = theForm.prev_column_index.options[i].value;
				theForm.prev_column_index.options[i].value = -1;
				for(var j=i+1;j<len;j++) {
					var currIndex = theForm.prev_column_index.options[j].value;					
					if(currIndex != -1) theForm.prev_column_index.options[j].value = nextIndex++;
				}
			}
			break;
		}
	}
}

function changeField(theForm)	{
	var index = theForm.column_names.options.selectedIndex;

	if(index >= 0) {
	
		var colName = theForm.field_name.value;
		var colType = theForm.field_type.value;
		var colSize = isNumber(theForm.field_size);
		var primary = (theForm.primary.checked) ? 1 : 0;
		var unique  = (theForm.unique.checked) ? 1 : 0;
		var notnull = (theForm.notnull.checked) ? 1 : 0;

		if(isEmpty(theForm.field_name));
		else if( colSize == -1) {
			alert("Field size must be a valid number");
			theForm.field_size.focus();
		}
		else {			
			if(theForm == document.alter_form) recordPrevAction(theForm,colName,index,1);

			theForm.column_names.options[index] = new Option(colName);
			theForm.column_names.options[index].value = colName;
			theForm.column_types.options[index] = new Option(colType);
			theForm.column_types.options[index].value = colType;
			theForm.column_sizes.options[index] = new Option(colSize);
			theForm.column_sizes.options[index].value = colSize;
			theForm.column_pk.options[index] = new Option(primary);
			theForm.column_pk.options[index].value = primary;
			theForm.column_un.options[index] = new Option(unique);
			theForm.column_un.options[index].value = unique;
			theForm.column_nn.options[index] = new Option(notnull);
			theForm.column_nn.options[index].value = notnull;
		}
	}
}

function removeField(theForm)	{
	var index   = theForm.column_names.options.selectedIndex;
	if(index >= 0) {
		var colName = theForm.column_names.options[index].value;
		if(theForm == document.alter_form) recordPrevAction(theForm,colName,index,-1);
		theForm.column_names.options[index] = null;
		theForm.column_types.options[index] = null;
		theForm.column_sizes.options[index] = null;
		theForm.column_pk.options[index] = null;
		theForm.column_un.options[index] = null;
		theForm.column_nn.options[index] = null;
	}
}

function callLoadForm(id) {
	document.load_form.tname.value = id.value;
	document.load_form.submit();
}

function submitForm(id) {
	var len = id.column_names.options.length;

	if(isEmpty(id.table_name));
	if(id == document.alter_form && id.table_name.value == 0) {
		alert("Select the table that you want to Modify.");
		id.table_name.focus();
	}
	else if(len < 1) {
		alert("Atleast one field must be entered.");
		id.field_name.focus();
	}
	else{
		for(var i=0;i<len;i++) {
			id.column_names.options[i].selected = true;
			id.column_types.options[i].selected = true;
			id.column_sizes.options[i].selected = true;
			id.column_pk.options[i].selected = true;
			id.column_un.options[i].selected = true;
			id.column_nn.options[i].selected = true;
		}

		if(id == document.alter_form) {
			len = id.prev_column_names.options.length;
			for(var i=0;i<len;i++) {
				id.prev_column_names.options[i].selected  = true;
				id.prev_column_index.options[i].selected  = true;
				id.prev_column_action.options[i].selected = true;
			}
		}
		id.submit();
	}
}

function setCheckBoxes(id1,id2) {
	var chk = id1.checked;
	var len = id2.length;
	if(typeof(len) != 'undefined') {
		for(var i=0;i<len;i++) id2[i].checked = chk;
	}
	else id2.checked = chk;
}

function submitEditForm(theForm,theList) {
	var sel = false;
	var len = theList.length;
	if(typeof(len) != 'undefined') {
		for(var i=0;i<len;i++) {
			if(theList[i].checked) {
				sel = true;
				break;
			}
		}
	}
	else sel = theList.checked;
	if(sel)	{
		theForm.action = 'EditForm';
		theForm.submit();
	}
	else alert("Select the records that you want to edit.");
}

function submitBDropForm(theForm,theList) {
	var sel = false;
	var len = theList.length;
	if(typeof(len) != 'undefined') {
		for(var i=0;i<len;i++) {
			if(theList[i].checked) {
				sel = true;
				break;
			}
		}
	}
	else sel = theList.checked;
	if(sel)	{
		var confirmed = confirm("Do you really want to delete \n the selected record(s).");
		if(confirmed) {
			theForm.action = 'DropRecords';
			theForm.submit();
		}
	}
	else alert("Select the records that you want to delete.");
}

function callEmptyTab(tableName) {
	var confirmed = confirm("Do you really want to delete \n" +
							"all records from '" + tableName + "' table.");
	if(confirmed) window.parent.right.location.href = "EmptyTab?table_name=" + tableName;
}

function callDropTab(tableName) {
	var confirmed = confirm("Do you really want to delete '" + tableName + "' table.");
	if(confirmed) window.parent.right.location.href = "DropTab?table_name=" + tableName;
}

function submitRenameForm(theForm) {
	var oldName = theForm.old_name.value;
	var newName = theForm.new_name.value;
	if(oldName == "none")	alert("Select the table that you want to rename.");
	else if(isEmpty(theForm.new_name));
	else if(oldName == newName) {
		alert("To rename a table, Enter a new name for the selected table.");
		theForm.new_name.focus();
	}
	else theForm.submit();
}

function submitDropForm(theForm) {
	var tableName = theForm.table_name.value;

	if(tableName == 0)	alert("Select the table that you want to delete.");
	else theForm.submit();
}

function submitDropColumnForm(theForm) {
	var columnName = theForm.column_name.value;

	if(columnName == 0)	alert("Select the column that you want to delete.");
	else theForm.submit();
}

function submitEmptyColumnForm(theForm,tempForm) {
	var index = theForm.column_name.options.selectedIndex;

	if(index <= 0)	alert("Select the column that you want to Empty.");
	else {
		var colSpec = tempForm.column_types.options[index-1].value;
		colSpec	+= "(" + tempForm.column_sizes.options[index-1].value + ")";
		colSpec += (tempForm.column_nn.options[index-1].value == 1) ? " not null" : "";
		theForm.column_spec.value = colSpec;
		theForm.submit();
	}
}

function submitRenameColumnForm(theForm,tempForm) {
	var oldName = theForm.old_name.value;
	var newName = theForm.new_name.value;

	if(oldName == 0)	alert("Select the column that you want to rename.");
	else if(isEmpty(theForm.new_name));
	else if(oldName == newName)	{
		alert("To rename a column, Enter a new name for the selected column.");
		theForm.new_name.focus();
	}
	else {
		var index	= theForm.old_name.options.selectedIndex;
		var colSpec = tempForm.column_types.options[index-1].value;
		colSpec	+= "(" + tempForm.column_sizes.options[index-1].value + ")";
		colSpec += (tempForm.column_nn.options[index-1].value == 1) ? " not null" : "";
		theForm.column_spec.value = colSpec;
		theForm.submit();
	}
}

function submitAlterColumnForm(theForm,tempForm) {
	var columnName = theForm.column_name.value;

	if(columnName == 0)	alert("Select the column that you want to modify.");
	else if(-1 == isNumber(theForm.column_size)) {
		alert("Column size must be a valid number");
		theForm.column_size.focus();
	}
	else theForm.submit();
}

function loadColData(theForm,tempForm,id) {
	index = id.options.selectedIndex;

	if(index == 0) theForm.reset();
	else if(index > 0) {
		theForm.column_type.value = tempForm.column_types.options[index-1].value;
		theForm.column_size.value = tempForm.column_sizes.options[index-1].value;
		theForm.primary.checked	  = (tempForm.column_pk.options[index-1].value == 1) ? true : false;
		theForm.notnull.checked	  = (tempForm.column_nn.options[index-1].value == 1) ? true : false;
		theForm.unique.checked	  = false;
	}
}

function submitQueryForm(theForm) {
	if(isEmpty(theForm.query));
	else theForm.submit();
}

function callExportDB(id) {
	var value = id.value;

	if(value != 0) window.parent.right.location.href = "ExportDB?table_name=" + value;
}

function submitExportForm(theForm) {
	var value = theForm.table_name.value;
	if(value == 0) alert("Select the table that you want to export.");
	else theForm.submit();
}

function submitImportForm(theForm) {
	var value = theForm.file_name.value;
	var ftype = value.substr(value.indexOf('.'),value.length);

	if(ftype != ".sql") {
		alert("Only \".sql\" files are allowed to import.");
		theForm.file_name.focus();
	}
	else theForm.submit();
}

function submitSearchForm(theForm) {
	if(isEmpty(theForm.keyword));
	else theForm.submit();
}