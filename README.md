var moveAmount = 5;
var moving = false;

// وقتی Space فشار داده می‌شه
window.addEventListener("keydown", function(e) {
    if (e.keyCode === 32) {
        moving = true;
    }
});

// وقتی Space رها می‌شه
window.addEventListener("keyup", function(e) {
    if (e.keyCode === 32) {
        moving = false;
    }
});

// در هر فریم بررسی کن
this.on("tick", function() {
    if (moving) {
        car.x += moveAmount; // حرکت به راست
    }
});