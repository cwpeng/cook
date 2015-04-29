var gm={"state":{}, "data":{}, "evts":{}, "ui":{}, "material":{}, "cookbook":{}, "geometry":{}, "base":{}};
window.onload=function(){
	gm.state.page="welcome";
};
gm.id=function(id){
	return document.getElementById(id);
};
gm.ajax=function(args){
	gm.ui.showMask();
	var req=new XMLHttpRequest();
	req.onload=function(){
		args.callback.apply(this); args=null;
		gm.ui.hideMask();
	};
	if(args.method.toLowerCase()=="post"){
		req.open(args.method, args.src, true);
		req.setRequestHeader("Content-type","application/x-www-form-urlencoded");
		req.send(args.args);
	}else{
		req.open(args.method, args.src+"?"+args.args, true);
		req.send();
	}
};
gm.createElement=function(tagName,settings,parentElement){
	var obj=document.createElement(tagName);
	if(settings.atrs){gm.setAttributes(obj,settings.atrs);}
	if(settings.stys){gm.setStyles(obj,settings.stys);}
	if(settings.evts){gm.setEventHandlers(obj,settings.evts);}
	if(parentElement instanceof Element){parentElement.appendChild(obj);}
	return obj;
};
gm.modifyElement=function(obj,settings,parentElement){
	if(settings.atrs){
		gm.setAttributes(obj,settings.atrs);
	}
	if(settings.stys){
		gm.setStyles(obj,settings.stys);
	}
	if(settings.evts){
		gm.setEventHandlers(obj,settings.evts);
	}
	if(parentElement===obj.parentNode){
		parentElement.removeChild(obj);
	}else if(parentElement instanceof Element){
		parentElement.appendChild(obj);
	}
	return obj;
};
gm.setStyles=function(obj,styles){
	for(var name in styles){
		obj.style[name]=styles[name];
	}
	return obj;
};
gm.setAttributes=function(obj,attributes){
	for(var name in attributes){
		obj[name]=attributes[name];
	}
	return obj;
};
gm.setEventHandlers=function(obj,eventHandlers,useCapture){
	for(var name in eventHandlers){
		if(eventHandlers[name] instanceof Array){
			for(var i=0;i<eventHandlers[name].length;i++){
				obj.addEventListener(name,eventHandlers[name][i],useCapture);
			}
		}else{
			obj.addEventListener(name,eventHandlers[name],useCapture);
		}
	}
	return obj;
};
gm.changePage=function(id, init){
	gm.id(gm.state.page).style.display="none";
	gm.id(id).style.display="block";
	gm.state.page=id;
	if(typeof init=="function"){
		init();
	}
};
/* Shared User Interface */
gm.ui.showMask=function(){
	gm.id("mask").style.display="block";
};
gm.ui.hideMask=function(){
	gm.id("mask").style.display="none";
};
/* Material Management */
gm.material.init=function(){
	if(!gm.data.materials){
		if(gm.data.geometrySets){
			gm.material.get();
		}else{
			gm.geometry.getSets(gm.material.init);
		}
	}
};
gm.material.create=function(form){
	if(form.name.value==""||form.description.value==""){
		return;
	}
	gm.ajax({"method":"post", "src":"/exe/data/CreateMaterial",
		"args":"name="+encodeURIComponent(form.name.value)+"&description="+encodeURIComponent(form.description.value)+"&geometry_set="+form.geometry_set.options[form.geometry_set.selectedIndex].value,
		"callback":function(){
			alert("Created");
			gm.data.materials=null;
			gm.material.get();
			form.reset();
			form=null;
		}
	});
};
gm.material.modify=function(form){
	if(form.name.value==""||form.description.value==""){
		return;
	}
	var geometrySetName=form.geometry_set.value.toLowerCase();
	var geometrySet=0;
	for(var i=0;i<gm.data.geometrySets.length;i++){
		if(geometrySetName==gm.data.geometrySets[i].name.toLowerCase()){
			geometrySet=gm.data.geometrySets[i].id;
			geometrySetName=gm.data.geometrySets[i].name;
			break;
		}
	}
	gm.ajax({"method":"post", "src":"/exe/data/ModifyMaterial",
		"args":"id="+form.materialId+"&name="+encodeURIComponent(form.name.value)+"&description="+encodeURIComponent(form.description.value)+"&geometry_set="+geometrySet,
		"callback":function(){
			if(geometrySet==0){
				form.geometry_set.value="";
			}else{
				form.geometry_set.value=geometrySetName;
			}
			alert("Modified");
			form=geometrySetName=geometrySet=null;
		}
	});
};
gm.material.del=function(id, index){
	if(!confirm("Are you sure?")){
		return;
	}
	gm.ajax({"method":"post", "src":"/exe/data/DeleteMaterial",
		"args":"id="+id,
		"callback":function(){
			alert("Deleted");
			gm.data.materials.splice(index, 1);
			gm.material.update();
			index=null;
		}
	});
};
gm.material.get=function(callback){
	// Get Data
	gm.ajax({"method":"get", "src":"/exe/data/GetMaterials", "args":"", "callback":function(){
		gm.data.materials=JSON.parse(this.responseText);
		gm.material.update();
		if(typeof callback=="function"){
			callback();
		}
		callback=null;
	}});
};
gm.material.update=function(){
	// Update material list in material page
	var list=gm.id("material-list");
	list.innerHTML="";
	var material;
	var form;
	for(var i=0;i<gm.data.materials.length;i++){
		material=gm.data.materials[i];
		form=gm.createElement("form", {"stys":{"backgroundColor":i%2==0?"#eeeeee":"#cccccc"}, "evts":{"submit":gm.evts.submitModifyMaterialForm}}, list);
		form.materialId=material.id;
		form.innerHTML="<div>Name <input class='small' type='text' name='name' value='"+material.name+"' /> "+
			"Description <input class='large' type='text' name='description' value='"+material.description+"' /></div>";
		form.innerHTML+="<div>Geometry Set <input class='small' type='text' name='geometry_set' value='"+gm.material.getGeometrySetNameById(material.geometrySet)+"' /></div>";
		if(material.cookbook>0){
			form.innerHTML+="<div><input type='submit' value='Modify' /> <input type='button' value='Delete("+material.cookbook+")' disabled='true' /></div>";
		}else{
			form.innerHTML+="<div><input type='submit' value='Modify' /> <input type='button' value='Delete(0)' onclick='gm.material.del("+material.id+", "+i+");' /></div>";
		}
	}
	// Update material checkboxs in cookbook page
	list=gm.id("cookbook-material-list");
	list.innerHTML="";
	for(var i=0;i<gm.data.materials.length;i++){
		material=gm.data.materials[i];
		if(i>0){
			list.innerHTML+="&nbsp;&nbsp; || &nbsp;";
		}
		list.innerHTML+=material.name+"<input type='checkbox' name='material"+material.id+"' /> <input type='number' value='1' min='1' max='10' step='1' class='tiny' name='material"+material.id+"number' />";
	}
};
	gm.material.getGeometrySetNameById=function(id){
		for(var i=0;i<gm.data.geometrySets.length;i++){
			if(gm.data.geometrySets[i].id==id){
				return gm.data.geometrySets[i].name;
			}
		}
		return "";
	};
	gm.evts.submitModifyMaterialForm=function(e){
		e.preventDefault();
		gm.material.modify(this);
	};
/* Cookbook Management */
gm.cookbook.init=function(){
	if(!gm.data.cookbooks){
		if(gm.data.materials){
			gm.cookbook.get();
		}else{
			gm.material.get(gm.cookbook.init);
		}
	}
};
gm.cookbook.get=function(){
	// Get Data
	gm.ajax({"method":"get", "src":"/exe/data/GetCookbooks", "args":"", "callback":function(){
		gm.data.cookbooks=JSON.parse(this.responseText);
		gm.cookbook.update();
	}});
};
gm.cookbook.update=function(){
	// Update cookbook list in cookbook page
	var list=gm.id("cookbook-list");
	list.innerHTML="";
	var cookbook, materials;
	var form;
	for(var i=0;i<gm.data.cookbooks.length;i++){
		cookbook=gm.data.cookbooks[i];
		form=gm.createElement("form", {"stys":{"backgroundColor":i%2==0?"#eeeeee":"#cccccc"}, "evts":{"submit":gm.evts.submitModifyCookbookForm}}, list);
		form.cookbookId=cookbook.id;
		form.innerHTML="<div>Name <input class='small' type='text' name='name' value='"+cookbook.name+"' /> "+
			"Description <input class='large' type='text' name='description' value='"+cookbook.description+"' /></div>";
		materials="";
		for(var j=0;j<cookbook.materials.length;j++){
			if(j>0){
				materials+="&nbsp; || ";
			}
			materials+=gm.cookbook.getMaterialNameById(cookbook.materials[j].id)+" <input type='number' value='"+cookbook.materials[j].number+"' min='1' max='10' step='1' class='tiny' name='material"+cookbook.materials[j].id+"number' />";
		}
		form.innerHTML+="<div>Materials "+materials+"</div>";
		form.innerHTML+="<div><input type='submit' value='Modify' /> <input type='button' value='Delete' onclick='gm.cookbook.del("+cookbook.id+", "+i+");' /></div>";
	}
};
	gm.cookbook.getMaterialNameById=function(id){
		if(!gm.data.materials){
			return null;
		}
		for(var i=0;i<gm.data.materials.length;i++){
			if(gm.data.materials[i].id==id){
				return gm.data.materials[i].name;
			}
		}
		return null;
	};
	gm.evts.submitModifyCookbookForm=function(e){
		e.preventDefault();
		gm.cookbook.modify(this);
	};
gm.cookbook.create=function(form){
	if(form.name.value==""||form.description.value==""){
		return;
	}
	var materials="";
	var materialId;
	for(var i=0;i<gm.data.materials.length;i++){
		materialId=gm.data.materials[i].id;
		if(form["material"+materialId].checked){
			if(materials.length>0){
				materials+=";";
			}
			materials+=materialId+":"+form["material"+materialId+"number"].value;
		}
	}
	if(materials==""){
		return;
	}
	gm.ajax({"method":"post", "src":"/exe/data/CreateCookbook",
		"args":"name="+encodeURIComponent(form.name.value)+"&description="+encodeURIComponent(form.description.value)+"&materials="+materials,
		"callback":function(){
			alert("Created");
			gm.data.cookbooks=null;
			gm.cookbook.get();
			form.reset();
			form=null;
		}
	});
};
gm.cookbook.modify=function(form){
	if(form.name.value==""||form.description.value==""){
		return;
	}
	var materials="";
	var materialId;
	for(var i=0;i<gm.data.materials.length;i++){
		materialId=gm.data.materials[i].id;
		if(form["material"+materialId+"number"]){
			if(materials.length>0){
				materials+=";";
			}
			materials+=materialId+":"+form["material"+materialId+"number"].value;
		}
	}
	gm.ajax({"method":"post", "src":"/exe/data/ModifyCookbook",
		"args":"id="+form.cookbookId+"&name="+encodeURIComponent(form.name.value)+"&description="+encodeURIComponent(form.description.value)+"&materials="+materials,
		"callback":function(){
			alert("Modified");
		}
	});
};
gm.cookbook.del=function(id, index){
	if(!confirm("Are you sure?")){
		return;
	}
	gm.ajax({"method":"post", "src":"/exe/data/DeleteCookbook",
		"args":"id="+id,
		"callback":function(){
			alert("Deleted");
			gm.data.cookbooks.splice(index, 1);
			gm.cookbook.update();
			index=null;
		}
	});
};
/* Geometry Management */
gm.geometry.init=function(){
	if(!gm.data.geometrySets){
		gm.geometry.getSets();
	}
};
gm.geometry.getSets=function(callback){
	// Get Data
	gm.ajax({"method":"get", "src":"/exe/data/GetGeometrySets", "args":"", "callback":function(){
		gm.data.geometrySets=JSON.parse(this.responseText);
		gm.geometry.updateSets();
		if(typeof callback=="function"){
			callback();
		}
		callback=null;
	}});
};
gm.geometry.updateSets=function(){
	// Update geometry set list in geometry page
	var list=gm.id("geometry-set-list");
	list.innerHTML="";
	var set;
	var form;
	for(var i=0;i<gm.data.geometrySets.length;i++){
		set=gm.data.geometrySets[i];
		form=gm.createElement("form", {"stys":{"backgroundColor":i%2==0?"#eeeeee":"#cccccc"}, "evts":{"submit":gm.evts.submitModifyGeometrySetForm}}, list);
		form.setId=set.id;
		form.innerHTML="<div>Name <input class='small' type='text' name='name' value='"+set.name+"' /> "+
			"Description <input class='large' type='text' name='description' value='"+set.description+"' /></div>";
		form.innerHTML+="<div>"+set.number+" Records <input type='submit' value='Modify' /> <input type='button' value='Delete' onclick='gm.geometry.delSet("+set.id+", "+i+");' /> <input type='button' value='View Data' onclick='gm.geometry.getSetData("+set.id+",false);' /> <input type='button' value='Download' onclick='gm.geometry.getSetData("+set.id+",true);' /></div>";
	}
	// Update geometry set list in material page
	list=gm.id("material-geometry-set-list");
	list.innerHTML="";
	list.add(gm.createElement("option", {"atrs":{"value":0, "textContent":""}}));
	for(var i=0;i<gm.data.geometrySets.length;i++){
		set=gm.data.geometrySets[i];
		list.add(gm.createElement("option", {"atrs":{"value":set.id, "textContent":set.name}}));
	}
};
	gm.evts.submitModifyGeometrySetForm=function(e){
		e.preventDefault();
		gm.geometry.modifySet(this);
	};
gm.geometry.createSet=function(form){
	if(form.name.value==""||form.description.value==""){
		return;
	}
	var src;
	for(var i=0;i<form.elements["src"].length;i++){
		if(form.elements["src"][i].checked){
			src=form.elements["src"][i].value;
			break;
		}
	}
	var lines=form.data.value.split(/\r\n|\n|\r/);
	var data="";
	for(var i=0;i<lines.length;i++){
		if(src=="input"){
			// Basic input format test
			if(lines[i].search(/[^0-9.,]/)>-1){
				alert("Format of latlng data you input is incorrect.");
				return;
			}
		}else{
			// Convert set name to id
			for(var j=0;j<gm.data.geometrySets.length;j++){
				if(lines[i]==gm.data.geometrySets[j].name){
					lines[i]=gm.data.geometrySets[j].id;
					break;
				}
				if(j==gm.data.geometrySets.length-1){
					alert("Set name you input dose not exist.");
					return;
				}
			}
		}
		if(i>0){
			data+=";";
		}
		data+=lines[i];
	}
	gm.ajax({"method":"post", "src":"/exe/data/CreateGeometrySet",
		"args":"name="+encodeURIComponent(form.name.value)+"&description="+encodeURIComponent(form.description.value)+"&data="+data+"&src="+src,
		"callback":function(){
			alert("Created");
			gm.data.geometrySets=null;
			gm.geometry.getSets();
			form.reset();
			form=null;
		}
	});
};
gm.geometry.modifySet=function(form){
	if(form.name.value==""||form.description.value==""){
		return;
	}
	gm.ajax({"method":"post", "src":"/exe/data/ModifyGeometrySet",
		"args":"id="+form.setId+"&name="+encodeURIComponent(form.name.value)+"&description="+encodeURIComponent(form.description.value),
		"callback":function(){
			alert("Modified");
		}
	});
};
gm.geometry.delSet=function(id, index){
	if(!confirm("Are you sure?")){
		return;
	}
	gm.ajax({"method":"post", "src":"/exe/data/DeleteGeometrySet",
		"args":"id="+id,
		"callback":function(){
			alert("Deleted");
			gm.data.geometrySets.splice(index, 1);
			gm.geometry.updateSets();
			index=null;
		}
	});
};
gm.geometry.getSetData=function(id, download){
	window.open("/exe/data/GetGeometrySet?id="+id+(download?"&download=true":""), "_blank");
};
/* Geometry Management */
gm.base.init=function(){
	if(!gm.data.baseSummary){
		gm.base.getSummary();
	}
};
gm.base.getSummary=function(){
	gm.ajax({"method":"post", "src":"/exe/data/GetMaterialBaseSummary",
		"args":"",
		"callback":function(){
			gm.data.baseSummary=JSON.parse(this.responseText);
			gm.base.updateSummary();
		}
	});
};
gm.base.generate=function(){
	if(window.confirm("Generate New Data?")){
		gm.ajax({"method":"post", "src":"/exe/data/GenerateMaterialBases",
			"args":"",
			"callback":function(){
				gm.base.getSummary();
				alert("Generated");
			}
		});
	}
};
gm.base.del=function(){
	if(window.confirm("Delete Data?")&&window.confirm("Sure?")){
		gm.ajax({"method":"post", "src":"/exe/data/DeleteMaterialBases",
			"args":"",
			"callback":function(){
				gm.base.getSummary();
				alert("Deleted");
			}
		});
	}
};
gm.base.updateSummary=function(){
	var summary=gm.data.baseSummary;
	var container=gm.id("base-summary");
	if(summary.generated){
		gm.id("base-generate-btn").disabled=true;
		gm.id("base-del-btn").disabled=false;
		container.innerHTML="Data Generated".bold()+"<br/>";
		if(summary.count>0){
			var time=new Date(summary.updateTime);
			time=time.getFullYear()+"-"+(time.getMonth()+1)+"-"+time.getDate()+" "+time.getHours()+":"+time.getMinutes()+":"+time.getSeconds();
			container.innerHTML+="Base Count: "+summary.count+"<br/>Update Time: "+time;
		}else{ // No Data
			container.innerHTML+="Statistical Data is Delayed (Only update once a day)";
		}
	}else{
		gm.id("base-generate-btn").disabled=false;
		gm.id("base-del-btn").disabled=true;
		container.innerHTML="Data Unavailable".bold();
	}
};