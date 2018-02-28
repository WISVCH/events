var ShoppingBasket;

(function ($) {

    ShoppingBasket = {

        LOCAL_STORAGE_SHOPPING_BASKET: 'shoppingBasket',

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

                $.each(ShoppingBasket.shoppingBasket, function (index, product) {
                    shoppingBasketTable += vsprintf("<tr><td>%s</td><td>%s</td><td>&euro; %s</td></tr>", [
                        product.title,
                        product.amount,
                        parseFloat(Math.round(product.amount * product.cost * 100) / 100).toFixed(2).replace(".", ",")
                    ]);

                    countItems += product.amount;

                    shoppingBasketTotal += product.amount * product.cost;
                });

                if (shoppingBasketTotal > 0) {
                    shoppingBasketTable += "<tr><td>Transaction fee</td><td>1</td><td>&euro; 0,35</td></tr>";
                    shoppingBasketTotal += 0.35;
                }

                $("#shoppingBasketTable").html(shoppingBasketTable);
                $("#shoppingBasketCount").html(countItems);
                $("#shoppingBasketTotal").html("&euro; " + parseFloat(Math.round(shoppingBasketTotal * 100) / 100).toFixed(2).replace(".", ","));
            }
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
        },

        __shakeShoppingBasket: function () {
            var shoppingBasketButton = $("#shoppingBasketButton");

            shoppingBasketButton.addClass('animated shake').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
                $(this).removeClass('animated shake');
            });
        }

    };
})
(jQuery);