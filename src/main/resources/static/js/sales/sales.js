$(document).ready(function () {

    $("input[type=number]").each(function () {
        $(this).val(0);
    });

    $(".decrease").on("click", function () {
        var el = $(this).parent().children("input[type=number]")[0];
        var amount = parseInt($(el).val());

        if (amount > 0) $(el).val(amount - 1);
    });

    $(".increase").on("click", function () {
        var el = $(this).parent().children("input[type=number]")[0];
        var amount = parseInt($(el).val());

        $(el).val(amount + 1);
    });

    $("#scanPage").keypress(function (e) {
        var token = $("#rfidToken");

        if (e.which === 13) {
            $("#formScan").submit();
            return false;
        }
        token.val(token.val() + e.key);
    });

    $("#scanProduct").keypress(function (e) {
        var token = $("#customerRFID");

        if (e.which === 13) {
            $("#formScanProduct").submit();
            return false;
        }
        token.val(token.val() + e.key);
    });
});
