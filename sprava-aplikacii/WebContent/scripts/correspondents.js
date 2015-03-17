/**
 * 
 */

function win_onload() {
    		var obj=document.body.getElementsByTagName("table")[0];
    		var row=obj.rows;
    		var i=0;
    		var j;
    		var cel=null;
    		for(;i<row.length;i++) {
    			cel=row[i].cells;
    			if(i==0){
    				cel[0].innerText.toLowerCase()=="correspondent"?cel[0].innerHTML="Korešpondent":{};
    				cel[1].innerText.toLowerCase()=="service"?cel[1].innerHTML="Služba":{};
    				cel[2].innerText.toLowerCase()=="r. status"?cel[2].innerHTML="Prijímanie správ":{};
    				cel[3].innerText.toLowerCase()=="r. validity"?cel[3].innerHTML="Platnosť autorizácie pre príjem správ":{};
    				cel[4].innerText.toLowerCase()=="s. status"?cel[4].innerHTML="Odosielanie správ":{};
    				cel[5].innerText.toLowerCase()=="s. validity"?cel[5].innerHTML="Platnosť autorizácie pre odoslielanie správ":{};
    				cel[6].innerText.toLowerCase()=="stav"?cel[6].innerHTML="Konečný stav":{};
    			}
    			else {
	    			//farebne odlisenie
	    			if(cel[2].innerText.toLowerCase()=="enabled"&&cel[3].innerText.toLowerCase()=="valid"&&cel[4].innerText.toLowerCase()=="enabled"&&cel[5].innerText.toLowerCase()=="valid"){for(j=0;j<cel.length;j++){cel[j].style.backgroundColor="";}cel[6].innerHTML = "OK";}
	    			if((cel[2].innerText.toLowerCase()=="enabled"&&cel[3].innerText.toLowerCase()=="valid")&&(cel[4].innerText.toLowerCase()!="enabled"||cel[5].innerText.toLowerCase()!="valid")){for(j=0;j<cel.length;j++){cel[j].style.backgroundColor="";}cel[0].style.backgroundColor="lightgray";cel[6].style.backgroundColor="lightgreen";cel[6].innerHTML = "Iba&nbsp;príjem&nbsp;správ";}
	    			if((cel[2].innerText.toLowerCase()!="enabled"&&cel[3].innerText.toLowerCase()!="valid")&&(cel[4].innerText.toLowerCase()=="enabled"||cel[5].innerText.toLowerCase()=="valid")){for(j=0;j<cel.length;j++){cel[j].style.backgroundColor="";}cel[0].style.backgroundColor="lightgray";cel[6].style.backgroundColor="lightyellow";cel[6].innerHTML = "Iba&nbsp;odosielanie&nbsp;správ";}
	    			if((cel[2].innerText.toLowerCase()!="enabled"||cel[3].innerText.toLowerCase()!="valid")&&(cel[4].innerText.toLowerCase()!="enabled"||cel[5].innerText.toLowerCase()!="valid")){for(j=0;j<cel.length;j++){cel[j].style.backgroundColor="";}cel[0].style.backgroundColor="lightgray";cel[6].style.backgroundColor="pink";cel[6].innerHTML = "Žiadne&nbsp;správy";}
	    			
	    			//definicia zmeny slov pre stlpec 1
	    			cel[1].innerText.toLowerCase()=="swift.fin"?cel[1].innerHTML=cel[1].innerText+"&nbsp;(P)":{};
	    			cel[1].innerText.toLowerCase()=="swift.fin!p"?cel[1].innerHTML="swift.fin!&nbsp;(T)":{};
	    			//definicia zmeny slov pre stlpec 2
	    			if(cel[2].innerText.toLowerCase()=="enabled") {
	    				cel[2].innerHTML="povolené";
	    			}
	    			else {
	    				cel[2].innerText.toLowerCase()==""?cel[2].innerHTML="nevymenené":{};
	    				cel[2].innerText.toLowerCase()=="rejected"?cel[2].innerHTML="odmietnuté":{};
	    				cel[2].innerText.toLowerCase()=="revoked"?cel[2].innerHTML="odovolané":{};
	    				cel[2].style.backgroundColor="lightgray";
	    			}
	    			//definicia zmeny slov pre stlpec 3
	    			if(cel[3].innerText.toLowerCase()=="valid") {
	    				cel[3].innerHTML="platná";
	    			}
	    			else {
	    				cel[3].innerText.toLowerCase()==""?cel[3].innerHTML="žiadna":{};
	    				cel[3].innerText.toLowerCase()=="invalid"?cel[3].innerHTML="neplatná":{};
	    				cel[3].innerText.toLowerCase()=="expired"?cel[3].innerHTML="vypršaná":{};
	    				cel[3].style.backgroundColor="lightgray";
	    			}
	    			//definicia zmeny slov pre stlpec 4
	    			if(cel[4].innerText.toLowerCase()=="enabled") {
	    				cel[4].innerHTML="povolené";
	    			}
	    			else {
	    				cel[4].innerText.toLowerCase()==""?cel[4].innerHTML="nevymenené":{};
	    				cel[4].innerText.toLowerCase()=="rejected"?cel[4].innerHTML="odmietnuté":{};
	    				cel[4].innerText.toLowerCase()=="revoked"?cel[4].innerHTML="odovolané":{};
	    				cel[4].style.backgroundColor="lightgray";
	    			}
	    			//definicia zmeny slov pre stlpec 5
	    			if(cel[5].innerText.toLowerCase()=="valid") {
	    				cel[5].innerHTML="platná";
	    			}
	    			else {
	    				cel[5].innerText.toLowerCase()==""?cel[5].innerHTML="žiadna":{};
	    				cel[5].innerText.toLowerCase()=="invalid"?cel[5].innerHTML="neplatná":{};
	    				cel[5].innerText.toLowerCase()=="expired"?cel[5].innerHTML="vypršaná":{};
	    				cel[5].style.backgroundColor="lightgray";
	    			}
    			}
    		}
    	}