$(document).ready(function () {
    $("#q").autocomplete({
        serviceUrl: '/api/v1/products/unused/search',
        showNoSuggestionNotice: true,
        noSuggestionNotice: "<div class=\"autocomplete-suggestion\" data-index=\"1\">No product found.</div>",
        lookupLimit: 3,
        minChars: 1,
        onSelect: function (suggestion) {
            var products = $("#products");
            var size = products.children().length;
            products.append('<input type="hidden" id="products' + size + '" name="products[' + size + ']" value="' + suggestion.data + '">');

            $("#productsTable").append("<tr><td><span class='label label-danger'>N.S.Y.</span> " + suggestion.value + "</td><td></td></tr>");
            $("#q").val("");
        }
    });

    $(".remove-product").click(function () {
        var products = $("#products").children();
        var productID = $(this).attr('data-product-id');

        $(this).parent().parent().parent().remove();
        for (var i = 0; i < products.length; i++) {
            if (productID == products[i].value) {
                products[i].remove();
            }
        }
    });

    $("#productDatatable").DataTable({
        searching: false,
        paging: false,
        info: false,
        columnDefs: [
            {
                width: "120px",
                targets: 1,
                orderable: false
            }
        ]
    });
});