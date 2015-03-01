var gm={"state":{}, "data":{}, "evts":{}, "material":{}, "cookbook":{}};
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
gm.material.get=function(){
	// Get Data
	gm.ajax({"method":"get", "src":"/exe/data/GetMaterials", "args":"", "callback":function(){
		gm.data.materials=JSON.parse(this.responseText);
		gm.material.update();
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
		form=gm.createElement("form", {"stys":{"backgroundColor":i%2==0?"#eeeeee":"#cccccc"}, "evts":{"submit":gm.evts.submitModifyForm}}, list);
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
		list.innerHTML+=material.name+"<input type='checkbox' name='material-"+material.id+"' /> <input type='number' value='1' min='1' max='10' step='1' class='tiny' name='material-"+material.id+"-number' />";
	}
};
	gm.evts.submitModifyForm=function(e){
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
	
};