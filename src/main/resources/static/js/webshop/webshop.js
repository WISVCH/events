var ShoppingBasket;

(function ($) {

    ShoppingBasket = {

        LOCAL_STORAGE_SHOPPING_BASKET: 'shoppingBasket',

        toast_id: 0,

        shoppingBasket: [],

        init: function () {
            if (Storages.localStorage.get(ShoppingBasket.LOCAL_STORAGE_SHOPPING_BASKET) !== null) {
                ShoppingBasket.shoppingBasket = Storages.localStorage.get(ShoppingBasket.LOCAL_STORAGE_SHOPPING_BASKET);
            }

            ShoppingBasket.__createShoppingBasketTable();
            ShoppingBasket.binds();
        },

        binds: function () {
            $("#emptyShoppingBasket").on('click', ShoppingBasket.__emptyShoppingBasket);
            $("#orderForm").on('submit', ShoppingBasket.__beforeShoppingBasketSubmit);
            $(".btn-shopping-basket").on('click', ShoppingBasket.__addProductToShoppingBasket);
        },

        __beforeShoppingBasketSubmit: function () {
            const orderProduct = '<input type="hidden" name="products[%s]" value="%s">';

            var count = 0;
            $.each(ShoppingBasket.shoppingBasket, function (index, ticket) {
                $("#products").append(vsprintf(orderProduct, [ticket.key, ticket.amount]));

                count++;
            });
        },

        __emptyShoppingBasket: function () {
            ShoppingBasket.shoppingBasket = [];
            Storages.localStorage.set(ShoppingBasket.LOCAL_STORAGE_SHOPPING_BASKET, []);
            ShoppingBasket.__createShoppingBasketTable();
        },

        __addProductToShoppingBasket: function (e) {
            e.preventDefault();
            var indexProduct = ShoppingBasket.__indexProductContains(
                $(e.target).data('product-key'), ShoppingBasket.shoppingBasket);

            if (indexProduct >= 0) {
                if (ShoppingBasket.shoppingBasket[indexProduct].amount < $(e.target).data('customer-limit')) {
                    ShoppingBasket.shoppingBasket[indexProduct].amount++;
                } else {
                    ShoppingBasket.__shakeShoppingBasket();
                    return false;
                }
            } else {
                ShoppingBasket.shoppingBasket.push({
                    "key": $(e.target).data('product-key'),
                    "title": $(e.target).data('product-title'),
                    "cost": $(e.target).data('product-cost'),
                    "amount": 1
                });
            }

            ShoppingBasket.__pulseShoppingBasket();
            Storages.localStorage.set(ShoppingBasket.LOCAL_STORAGE_SHOPPING_BASKET, ShoppingBasket.shoppingBasket);
            ShoppingBasket.__createShoppingBasketTable();
        },

        __createShoppingBasketTable: function () {
            if (ShoppingBasket.shoppingBasket.length === 0) {
                ShoppingBasket.__createEmptyShoppingBasketTable();
            } else {
                var shoppingBasketTable = "";
                var shoppingBasketTotal = 0;
                var countItems = 0;
                var administrationCosts = 0;

                $.each(ShoppingBasket.shoppingBasket, function (index, product) {
                    var rowBlueprint = "<tr><td>%s</td><td><a href='#' class='decreaseBasketAmount' data-product-key='%s'><i class='fas fa-minus'></i></a><span class='px-4'>%s</span><a href='#' class='increaseBasketAmount' data-product-key='%s'><i class='fas fa-plus'></i></a></td><td>&euro; %s</td></tr>";

                    shoppingBasketTable += vsprintf(rowBlueprint, [
                        product.title,
                        product.key,
                        product.amount,
                        product.key,
                        parseFloat(Math.round(product.amount * product.cost * 100) / 100).toFixed(2).replace(".", ",")
                    ]);

                    countItems += product.amount;

                    shoppingBasketTotal += product.amount * product.cost;

                    if (product.cost > 0) {
                        administrationCosts = administrationCostsSetting;
                    }
                });

                var rowBlueprint = "<tr><td>Administration costs</td><td><td>&euro; %s</td></tr>";

                shoppingBasketTable += vsprintf(rowBlueprint, [
                    parseFloat(Math.round(administrationCosts * 100) / 100).toFixed(2).replace(".", ",")
                ]);

                shoppingBasketTotal += administrationCosts;

                $("#shoppingBasketTable").html(shoppingBasketTable);
                $("#shoppingBasketCount").html(countItems);
                $("#shoppingBasketTotal").html("&euro; " + parseFloat(Math.round(shoppingBasketTotal * 100) / 100).toFixed(2).replace(".", ","));

                $('.increaseBasketAmount').on('click', ShoppingBasket.__increaseProductAmount);
                $('.decreaseBasketAmount').on('click', ShoppingBasket.__decreaseProductAmount);
            }
        },

        __increaseProductAmount: function (e) {
            e.preventDefault();
            var indexProduct = ShoppingBasket.__indexProductContains(
                $(e.currentTarget).data('productKey'), ShoppingBasket.shoppingBasket);

            ShoppingBasket.shoppingBasket[indexProduct].amount++;

            // Update ShoppingBasket
            Storages.localStorage.set(ShoppingBasket.LOCAL_STORAGE_SHOPPING_BASKET, ShoppingBasket.shoppingBasket);
            ShoppingBasket.__createShoppingBasketTable();
        },

        __decreaseProductAmount: function (e) {
            e.preventDefault();
            var indexProduct = ShoppingBasket.__indexProductContains(
                $(e.currentTarget).data('productKey'), ShoppingBasket.shoppingBasket);


            if (ShoppingBasket.shoppingBasket[indexProduct].amount > 1) {
                ShoppingBasket.shoppingBasket[indexProduct].amount--;
            } else {
                ShoppingBasket.shoppingBasket.splice(indexProduct, 1);
            }

            // Update ShoppingBasket
            Storages.localStorage.set(ShoppingBasket.LOCAL_STORAGE_SHOPPING_BASKET, ShoppingBasket.shoppingBasket);
            ShoppingBasket.__createShoppingBasketTable();
        },

        __createEmptyShoppingBasketTable: function () {
            $("#shoppingBasketCount").html(0);
            $("#shoppingBasketTable").html("<tr><td colspan='3'>Shopping basket is empty!</td></tr>");
            $("#shoppingBasketTotal").html("");
        },

        __indexProductContains: function (key, list) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].key === key) {
                    return i;
                }
            }

            return -1;
        },

        __pulseShoppingBasket: function () {
            var shoppingBasketButton = $("#shoppingBasketButton");

            shoppingBasketButton.addClass('animated pulse').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
                $(this).removeClass('animated pulse');
            });

            ShoppingBasket.__toast("Product added to your shopping basket.")
        },

        __shakeShoppingBasket: function () {
            var shoppingBasketButton = $("#shoppingBasketButton");

            shoppingBasketButton.addClass('animated shake').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
                $(this).removeClass('animated shake');
            });

            ShoppingBasket.__toast("You reached the maximum sold per customer.")
        },

        __toast: function (text) {
            var x = document.createElement('div');
            x.className = "toast show";
            x.innerHTML = text;

            document.body.appendChild(x);
            setTimeout(function () {
                x.className = x.className.replace("show", "");
            }, 3000);
        }

    };
})
(jQuery);

$(document).ready(function () {
    $('[data-markdown="enable"]').each(function(){
        $(this).html(window.markdownit().renderInline($(this).text()));
    });
});