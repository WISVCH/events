/**
 * Created by svenp on 31/07/2017.
 */
var TemplateSelector;

(function ($) {

    TemplateSelector = {
        init: function () {
            TemplateSelector.binds();
        },

        binds: function () {
            $(".product-template-item a").on('click', TemplateSelector.__setProductTemplateValues);
        },

        __setProductTemplateValues: function (e) {
            e.preventDefault();
            var data = $(e.target).parent().data('template');

            $.each(data, function (key, value) {
                if (key === "chOnly") {
                    key = key + "1";
                }
                var inputBox = $("#" + key);
                inputBox.val(value);

                if (key === "chOnly1" && value === true) {
                    inputBox.attr("checked", "checked");
                } else {
                    inputBox.removeAttr("checked");
                }
            });
        }
    };
})
(jQuery);

$(document).ready(function () {

    TemplateSelector.init();

    var config = {
        enableTime: true,
        altInput: true,
        altFormat: 'F j, Y H:i',
        dateFormat: "Y-m-dTH:i:S",
        time_24hr: true,
        locale: {
            firstDayOfWeek: 1
        }
    };
    $('#sellStart').flatpickr(config);
    $('#sellEnd').flatpickr(config);

    $('#q').autocomplete({
        serviceUrl: '/events/api/v1/products/search/unused',
        onSelect: function (suggestion) {
            addProductToEvent(suggestion.data, suggestion.value);
            $(this).val('');
            $('#addProduct').modal('hide');
        }
    });

    // Check if parentProduct exists
    hasParentFields($('#parentProduct').val() !== '' && $('#parentProduct').val() !== undefined);
    $('#parentProduct').on('change', function () {
        const hasParent = $(this).val() !== '';
        hasParentFields(hasParent);
    });

    function hasParentFields(hasParent) {
        $('#maxSold').prop('disabled', hasParent);
        $('#maxSoldPerCustomer').prop('disabled', hasParent);
        $('#productAvailability').toggle(!hasParent);
        $('#parentProductHint').toggle(hasParent);
    }

    $('.remove-product').on('click', function (e) {
        e.preventDefault();

        var products = $("#products");
        var size = products.children().length;

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

    function addProductToEvent(product_id, product_title) {
        const productInput = '<input type="hidden" id="products{0}" name="products[{1}]" value="{2}">';
        const productTableRow = "<tr><td><span class='fa fa-exclamation-triangle' data-toggle='tooltip' data-placement='right' title='Do not forget to update the event!'></span> {0}</td><td style='width: 40px;'><a class='btn btn-xs btn-danger remove-product text-white' data-product-id='{1}'><i class='fa fa-remove'></i></a></td></tr>";

        var products = $("#products");
        var count = products.children().length;

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

function format(source, params) {
    $.each(params, function (i, n) {
        source = source.replace(new RegExp("\\{" + i + "\\}", "g"), n);
    });
    return source;
}
