$(document).ready(function () {
    $("#q").autocomplete({
        serviceUrl: '/products/unused/search',
        showNoSuggestionNotice: true,
        noSuggestionNotice: "<div class=\"autocomplete-suggestion\" data-index=\"1\">No product found.</div>",
        lookupLimit: 3,
        minChars: 3,
        onSelect: function (suggestion) {
            $("#addProductKey").val(suggestion.data);
            $("#addProduct").submit();
        }
    });

    $(".remove-product").click(function () {
        $("#deleteProductKey").val($(this).data('id'));
        $("#deleteProduct").submit();
    });

    var timer;
    $("#editEvent .form-control").not(".no-trigger").keyup(function () {
        startTimer($("#editEvent"));
    }).keydown(function () {
        clearTimeout(timer);
    });


    $("#editEventOptions .form-control").change(function () {
        startTimer($("#editEventOptions"));
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
