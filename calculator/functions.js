var sum=0;
var product=1;
var diff=0;
var quot=1;
var po=1;
var op;
var insert=false;
function put(num) {
  var input=document.getElementById("number").value;
  if (num==10) {
  input+=".";
  insert=false;
  }
  else {
  if ((input=="0") || (insert==true)) {
  input=num;
  insert=false;
  }
  else 
  input+=num;
  }
  document.getElementById("number").value=input;
}

function fequal() {
  if (op=="+") fadd();
  if (op=="-") fsub();
  if (op=="*") fmult();
  if (op=="/") fdivd();
  if (op=="pow") power();
  insert=true;
}
function fadd() {
  product=1;
  diff=0;
  quot=1;
  po=1;
  sum+=parseFloat(document.getElementById("number").value);
  document.getElementById("number").value=sum;
  op="+";
  insert=true;
}
function fsub() {
  product=1;
  sum=0;
  quot=1;
  po=1;
  if (diff==0) diff=parseFloat(document.getElementById("number").value);
  else {
  diff-=parseFloat(document.getElementById("number").value);
  document.getElementById("number").value=diff;
  }
  op="-";
  insert=true;
}
function fmult() {
  sum=0;
  diff=0;
  quot=1;
  po=1;
  product*=parseFloat(document.getElementById("number").value);
  document.getElementById("number").value=product;
  op="*";
  insert=true;
}
function fdivd() {
  sum=0;
  diff=0;
  product=1;
  po=1;
  if (quot==1) quot=parseFloat(document.getElementById("number").value);
  else {
  quot/=parseFloat(document.getElementById("number").value);
  document.getElementById("number").value=quot;
  }
  op="/";
  insert=true;
}
function clearAll() {
  document.getElementById("number").value=0;
  sum=0;
  diff=0;
  product=1;
  quot=1;
  po=1;
  op="undefined";
}
function squareRoot() {
  sum=0;
  diff=0;
  product=1;
  quot=1;
  po=1;
  document.getElementById("number").value=Math.sqrt(parseFloat(document.getElementById("number").value));
  insert=true;
}
function getPi() {
  sum=0;
  diff=0;
  product=1;
  quot=1;
  po=1;
  document.getElementById("number").value=Math.PI;
  insert=true;
}
function getEuler() {
  sum=0;
  diff=0;
  product=1;
  quot=1;
  po=1;
  document.getElementById("number").value=Math.E;
  insert=true;
}
function expo() {
  sum=0;
  diff=0;
  product=1;
  quot=1;
  po=1;
  document.getElementById("number").value=Math.exp(parseFloat(document.getElementById("number").value));
  insert=true;
}
function natLog() {
  sum=0;
  diff=0;
  product=1;
  quot=1;
  po=1;
  document.getElementById("number").value=Math.log(parseFloat(document.getElementById("number").value));
  insert=true;
}
function log10() {
  sum=0;
  diff=0;
  product=1;
  quot=1;
  po=1;
  document.getElementById("number").value=Math.exp(parseFloat(document.getElementById("number").value))/Math.exp(10);
  insert=true;
}
function power() {
  sum=0;
  diff=0;
  product=1;
  quot=1;
  if (po==1) {
  po=parseFloat(document.getElementById("number").value);
  }
  else {
  document.getElementById("number").value=Math.pow(po,parseFloat(document.getElementById("number").value));
  }
  op="pow";
  insert=true;
}
function backspace() {
  if (document.getElementById("number").value!="0") {
  var str=document.getElementById("number").value;
  document.getElementById("number").value=str.slice(0,str.length-1);
  }
}
  
  