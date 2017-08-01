/**
 * Created by svenp on 31/07/2017.
 */
$(document).ready(function () {
    var config = {
        enableTime: true,
        altInput: true,
        altFormat: 'F j, Y H:i',
        dateFormat: "Y-m-dTH:i:S",
        time_24hr: true
    };
    $('#ending').flatpickr(config);
    $('#start').flatpickr(config);

    $('#q').autocomplete({
        serviceUrl: '/events/api/v1/products/search/unused',
        onSelect: function (suggestion) {
            addProductToEvent(suggestion.data, suggestion.value);
            $(this).val('');
            $('#addProduct').modal('hide');
        }
    });

    $('.remove-product').on('click', function (e) {
        e.preventDefault();

        var products = $("#products");
        var size = products.children().size();

        var removeProductId = $(this).data('product-id');
        var removeProductInput = products.find(":input[value='" + removeProductId + "']");
        var start = removeProductInput.attr('id').replace("products", "");

        // Remove input row in table
        removeProductInput.remove();
        $(this).parent().parent().remove();

        // Reset index of all the following products in the list.
        for (var i = start; i < size; i++) {
            var followingProduct = products.find("#products" + i);

            followingProduct.attr('id', 'products' + (i - 1));
            followingProduct.attr('name', 'products[' + (i - 1) + ']');
        }
    });

    $("#createNewProductButton").on('click', function (e) {
        e.preventDefault();

        var fail = assertRequiredFields();
        if (fail) {

        } else {
            var data = {
                title: $("#productTitle").val(),
                description: $("#productDescription").val(),
                cost: $("#productCost").val(),
                maxSold: $("#maxSold").val(),
                maxSoldPerCustomer: $("#maxSoldPerCustomer").val()
            };

            $.ajax({
                url: '/events/api/v1/products',
                type: 'POST',
                headers: {
                    "X-CSRF-TOKEN": $('input[name="_csrf"]').val(),
                    'Content-Type': "application/json"
                },
                dataType: 'json',
                cache: false,
                data: JSON.stringify(data),
                success: function (data) {
                    addProductToEvent(data.object.product_id, data.object.product_title);

                    $('#addProduct').modal('hide');
                },
                error: function (data) {

                }
            })
        }
    });

    function assertRequiredFields() {
        var fail = false;

        $('#addProductForm').find('select, textarea, input').each(function () {
            if (!$(this).hasClass('required')) {

            } else {
                if (!$(this).val()) {
                    $(this).parent().addClass('has-error');
                    fail = true;
                } else {
                    $(this).parent().removeClass('has-error');
                }
            }
        });
        return fail;
    }

    function addProductToEvent(product_id, product_title) {
        const productInput = '<input type="hidden" id="products{0}" name="products[{1}]" value="{2}">';
        const productTableRow = "<tr><td><span class='fa fa-exclamation-triangle' data-toggle='tooltip' data-placement='right' title='Do not forget to update the event!'></span> {0}</td><td style='width: 40px;'><a class='btn btn-xs btn-danger remove-product' data-product-id='{1}'><i class='glyphicon glyphicon-remove'></i></a></td></tr>";

        var products = $("#products");
        var count = products.children().size();

        $("#productsTable").append(format(productTableRow, [product_title, product_id]));
        products.append(format(productInput, [count, count, product_id]));
        $("#noProducts").remove();

        initTooltips();
    }

    function initTooltips() {
        $(function () {
            $('[data-toggle="tooltip"]').tooltip()
        });
    }
});