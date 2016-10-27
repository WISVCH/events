/**
 * Created by sven on 26/10/2016.
 */
$(document).ready(function () {
    var timer;
    $("#productEdit .form-control").not(".no-trigger").keyup(function () {
        startTimer($("#productEdit"));
    }).keydown(function () {
        clearTimeout(timer);
    });

    function startTimer(form) {
        clearTimeout(timer);
        timer = setTimeout(function () {
            if (form[0].checkValidity()) {
                form.submit();
            }
        }, 1000);
    }
});