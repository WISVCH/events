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
        const orderProduct = '<input type="hidden" name="products[{0}]" value="{1}">';

        $("input[type=number]").each(function () {
            if ($(this).val() > 0) {
                $("#products").append(format(orderProduct, [
                        $(this).data('product-key'),
                        $(this).val()
                    ]
                ));
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