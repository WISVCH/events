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

    $("#orderForm").on('submit', function () {
        const productInput = '<input type="hidden" id="products{0}" name="products[{1}]" value="{2}">';
        var count = 0;

        $("input[type=number]").each(function () {
            for (var i = 0; i < parseInt($(this).val()); i++) {
                var productId = $(this).data('product-id');

                $("#products").append(format(productInput, [count, count, productId]));
                count++;
            }
        });
    });
});

function format(source, params) {
    $.each(params, function (i, n) {
        source = source.replace(new RegExp("\\{" + i + "\\}", "g"), n);
    });
    return source;
}