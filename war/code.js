var gm={"state":{}, "data":{}, "evts":{}, "material":{}, "cookbook":{}, "geometry":{}};
window.onload=function(){
	gm.state.page="welcome";
};
gm.id=function(id){
	return document.getElementById(id);
};
gm.ajax=function(args){
	var req=new XMLHttpRequest();
	req.onload=args.callback;
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
/* Material Management */
gm.material.init=function(){
	if(!gm.data.materials){
		gm.material.get();
	}
};
gm.material.create=function(form){
	if(form.name.value==""||form.description.value==""){
		return;
	}
	gm.ajax({"method":"post", "src":"/exe/data/CreateMaterial",
		"args":"name="+encodeURIComponent(form.name.value)+"&description="+encodeURIComponent(form.description.value),
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
	gm.ajax({"method":"post", "src":"/exe/data/ModifyMaterial",
		"args":"id="+form.materialId+"&name="+encodeURIComponent(form.name.value)+"&description="+encodeURIComponent(form.description.value),
		"callback":function(){
			alert("Modified");
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
	gm.evts.submitModifyMaterialForm=function(e){
		e.preventDefault();
		gm.material.modify(this);
	};
/* Cookbook Management */
gm.cookbook.init=function(){
	if(!gm.data.cookbooks){
		gm.cookbook.get();
	}
};
gm.cookbook.get=function(){
	if(!gm.data.materials){
		gm.material.get(gm.cookbook.get);
		return;
	}
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
gm.geometry.getSets=function(){
	// Get Data
	gm.ajax({"method":"get", "src":"/exe/data/GetGeometrySets", "args":"", "callback":function(){
		gm.data.geometrySets=JSON.parse(this.responseText);
		gm.geometry.updateSets();
	}});
};
gm.geometry.updateSets=function(){
	// Update cookbook list in cookbook page
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
		form.innerHTML+="<div>"+set.number+" Records <input type='submit' value='Modify' /> <input type='button' value='Delete' onclick='gm.geometry.delSet("+set.id+", "+i+");' /></div>";
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
		if(src=="input"){ // Basic format test
			if(lines[i].search(/[^0-9.,]/)>-1){
				return;
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