Empty// ---------- Simple Calculator for Adobe Animate (HTML5 Canvas)
// Paste this into the Actions panel on frame 1.
// Requires instance names on stage: txtDisplay, btn0..btn9, btnDot, btnPlus, btnMinus, btnMul, btnDiv, btnEqual, btnClear, btnBack

this.stop(); // اگر نیازی به timeline نیست

// helper: get display object (Dynamic Text)
var display = this.txtDisplay;
if(!display) {
    alert("Error: txtDisplay not found. Make sure you set the instance name for the Dynamic Text field to 'txtDisplay'.");
}

// internal expression string
var expr = "";

// update visible display (limits length)
function refreshDisplay() {
    var shown = expr === "" ? "0" : expr;
    // crop if too long
    if(shown.length > 24) shown = shown.slice(-24);
    display.text = shown;
}

// safe tokenizer
function tokenize(s) {
    var tokens = [];
    var i = 0;
    while(i < s.length) {
        var ch = s[i];
        if(/\d|\./.test(ch)) {
            var num = ch;
            i++;
            while(i < s.length && /[\d.]/.test(s[i])) { num += s[i++]; }
            tokens.push(num);
            continue;
        }
        if(/[+\-*/()]/.test(ch)) {
            tokens.push(ch);
            i++;
            continue;
        }
        // skip spaces
        i++;
    }
    return tokens;
}

// shunting-yard to RPN
function toRPN(tokens) {
    var out = [], ops = [];
    var prec = { '+':1, '-':1, '*':2, '/':2 };
    for(var i=0;i<tokens.length;i++){
        var t = tokens[i];
        if(/^[\d.]+$/.test(t)) { out.push(t); continue; }
        if(t in prec) {
            while(ops.length) {
                var o2 = ops[ops.length-1];
                if(o2 in prec && prec[o2] >= prec[t]) out.push(ops.pop());
                else break;
            }
            ops.push(t);
            continue;
        }
        if(t === '(') { ops.push(t); continue; }
        if(t === ')') {
            while(ops.length && ops[ops.length-1] !== '(') out.push(ops.pop());
            if(ops.length && ops[ops.length-1] === '(') ops.pop();
            else throw "Mismatched parentheses";
            continue;
        }
    }
    while(ops.length) {
        var op = ops.pop();
        if(op === '(' || op === ')') throw "Mismatched parentheses";
        out.push(op);
    }
    return out;
}

// evaluate RPN
function evalRPN(rpn) {
    var st = [];
    for(var i=0;i<rpn.length;i++){
        var t = rpn[i];
        if(/^[\d.]+$/.test(t)) st.push(parseFloat(t));
        else {
            if(st.length < 2) throw "Invalid expression";
            var b = st.pop(), a = st.pop();
            var res = 0;
            if(t === '+') res = a + b;
            else if(t === '-') res = a - b;
            else if(t === '*') res = a * b;
            else if(t === '/') {
                if(b === 0) throw "Division by zero";
                res = a / b;
            } else throw "Unknown op";
            st.push(res);
        }
    }
    if(st.length !== 1) throw "Invalid expression";
    return st[0];
}

// evaluate full expression string safely
function evaluateExpression(s) {
    if(s.trim() === "") return 0;
    var tokens = tokenize(s.replace(/×/g,'*').replace(/÷/g,'/'));
    var rpn = toRPN(tokens);
    var result = evalRPN(rpn);
    // trim long decimals
    if(!Number.isInteger(result)) {
        result = parseFloat(result.toPrecision(12)); // avoid floating noise
    }
    return result;
}

// attach handler generator for buttons
function addBtnHandler(instanceName, handlerFn) {
    try {
        var btn = this[instanceName];
        if(btn) {
            btn.cursor = "pointer";
            btn.addEventListener("click", handlerFn.bind(this));
        } else {
            // console.warn("Button not found:", instanceName);
        }
    } catch(e) {
        console.error(e);
    }
}

// digit / dot handler
function appendInput(ch) {
    // prevent multiple dots in a number segment
    var lastNumber = expr.split(/[\+\-\*\/\(\)]/).pop();
    if(ch === '.' && lastNumber.indexOf('.') !== -1) return;
    expr += ch;
    refreshDisplay();
}

// operator handler (avoid consecutive operators)
function appendOperator(op) {
    if(expr === "" && (op === '+' || op === '-')) {
        // allow unary plus/minus by prefixing 0
        expr = "0" + op;
    } else {
        // replace trailing operator with new one
        if(/[+\-*/]$/.test(expr)) {
            expr = expr.slice(0, -1) + op;
        } else {
            expr += op;
        }
    }
    refreshDisplay();
}

// backspace
function backspace() {
    if(expr.length > 0) expr = expr.slice(0, -1);
    refreshDisplay();
}

// clear
function clearAll() {
    expr = "";
    refreshDisplay();
}

// equals
function equals() {
    try {
        var res = evaluateExpression(expr);
        expr = String(res);
        refreshDisplay();
    } catch(e) {
        display.text = "Error";
        expr = "";
    }
}

// initialize display
refreshDisplay();

// register handlers for digits and dot
for(var d=0; d<=9; d++) {
    addBtnHandler.call(this, "btn" + d, (function(c){ return function(){ appendInput(c); }; })(String(d)));
}
addBtnHandler.call(this, "btnDot", function(){ appendInput("."); });

// operators
addBtnHandler.call(this, "btnPlus", function(){ appendOperator("+"); });
addBtnHandler.call(this, "btnMinus", function(){ appendOperator("-"); });
addBtnHandler.call(this, "btnMul", function(){ appendOperator("*"); });
addBtnHandler.call(this, "btnDiv", function(){ appendOperator("/"); });

// control buttons
addBtnHandler.call(this, "btnBack", function(){ backspace(); });
addBtnHandler.call(this, "btnClear", function(){ clearAll(); });
addBtnHandler.call(this, "btnEqual", function(){ equals(); });

// optional: keyboard support
window.addEventListener("keydown", function(e){
    var k = e.key;
    if(/\d/.test(k)) appendInput(k);
    else if(k === ".") appendInput(".");
    else if(k === "+" || k === "-" || k === "*" || k === "/") appendOperator(k);
    else if(k === "Enter" || k === "=") { equals(); e.preventDefault(); }
    else if(k === "Backspace") backspace();
    else if(k === "Escape") clearAll();
}.bind(this));