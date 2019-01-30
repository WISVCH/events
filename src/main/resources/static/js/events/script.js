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
            $(".event-template-item a").on('click', TemplateSelector.__setEventTemplateValues);
            $(".product-template-item a").on('click', TemplateSelector.__setProductTemplateValues);
        },

        __setEventTemplateValues: function (e) {
            e.preventDefault();
            var data = $(e.target).parent().data('template');

            $.each(data, function (key, value) {
                $("#" + key).val(value);
            });

            TemplateSelector.__setCategories(data);
            TemplateSelector.__setTimes(data);
        },

        __setProductTemplateValues: function (e) {
            e.preventDefault();
            var data = $(e.target).parent().data('template');

            $.each(data, function (key, value) {
                var Key = key.replace(/\b\w/g, function (l) {
                    return l.toUpperCase()
                });
                var inputBox = $("#product" + Key);
                inputBox.val(value);

                if (key === "chOnly" && value === true) {
                    inputBox.attr("checked", "checked");
                } else {
                    inputBox.removeAttr("checked");
                }

                if (key === "reservable" && value === true) {
                    inputBox.attr("checked", "checked");
                } else {
                    inputBox.removeAttr("checked");
                }
            });
        },

        __setCategories: function (data) {
            $("input[name='categories']").each(function () {
                if ($.inArray($(this).val(), data.categories) >= 0) {
                    $(this).attr("checked", "checked");
                } else {
                    $(this).removeAttr("checked");
                }
            });
        },

        __setTimes: function (data) {
            document.querySelector("#start")._flatpickr.setDate(data.startingTime);
            document.querySelector("#ending")._flatpickr.setDate(data.endingTime);
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
        time_24hr: true
    };
    $('#ending').flatpickr(config);
    $('#start').flatpickr(config);

    $('#productSellStart').flatpickr(config);
    $('#productSellEnd').flatpickr(config);

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

    $("#createNewProductButton").on('click', function (e) {
        e.preventDefault();

        var fail = assertRequiredFields();
        if (fail) {
            $('#newProductModalError').show();
        } else {
            var data = {
                title: $("#productTitle").val(),
                description: $("#productDescription").val(),
                cost: $("#productCost").val(),
                maxSold: $("#maxSold").val(),
                maxSoldPerCustomer: $("#productMaxSoldPerCustomer").val(),
                sellStart: $("#productSellStart").val() === '' ? null : $("#productSellStart").val(),
                sellEnd: $("#productSellEnd").val() === '' ? null : $("#productSellEnd").val(),
                chOnly: $("#productChOnly").prop('checked'),
                reservable: $("#productReservable").prop('checked')
            };

            $.ajax({
                url: '/events/api/v1/products',
                type: 'POST',
                headers: {
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
                    alert(data.message);
                }
            })
        }
    });

    function assertRequiredFields() {
        var fail = false;

        $('#newProductModalError').hide();

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
        var productInput = '<input type="hidden" id="products{0}" name="products[{1}]" value="{2}">';
        var productTableRow = "<tr><td><span class='fa fa-exclamation-triangle' data-toggle='tooltip' data-placement='right' title='Do not forget to update the event!'></span> {0}</td><td style='width: 40px;'><a class='btn btn-xs btn-danger text-white remove-product' data-product-id='{1}'><i class='fas fa-trash-alt'></i></a></td></tr>";

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