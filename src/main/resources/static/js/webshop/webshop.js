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
            const orderProductKey = '<input type="hidden" id="products%s.productKey" name="products[%s].productKey" value="%s">';
            const orderProductOptionKey = '<input type="hidden" id="products%s.productOptionKey" name="products[%s].productOptionKey" value="%s">';
            const orderAmount = '<input type="hidden" id="products%s.amount" name="products[%s].amount" value="%s">';

            var count = 0;
            $.each(ShoppingBasket.shoppingBasket, function (index, ticket) {
                var products = $("#products");
                products.append(vsprintf(orderProductKey, [count, count, ticket.key]));
                products.append(vsprintf(orderProductOptionKey, [count, count, ticket.additionalKey]));
                products.append(vsprintf(orderAmount, [count, count, ticket.amount]));

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
            var product = $(e.target).parent().parent();
            var productKey = product.data('public-reference');
            var productAmount = parseInt(product.find('.product-amount').val());

            var productOption = product.find('.product-option');
            var productOptionKey = productOption.val();
            var productOptionTitle = productOption.find('option:selected').text();
            var productOptionPrice = productOption.find('option:selected').data('product-option-price');

            if (productOption.length > 0 && (productOptionKey === "" || productOptionKey === undefined)) {
                ShoppingBasket.__shakeShoppingBasket("Please select an product option!");
                return false;
            }

            var indexProduct = ShoppingBasket.__indexProductContains(productKey, productOptionKey, ShoppingBasket.shoppingBasket);

            if (indexProduct >= 0) {
                if (ShoppingBasket.shoppingBasket[indexProduct].amount + productAmount < product.data('product-user-limit')) {
                    ShoppingBasket.shoppingBasket[indexProduct].amount += productAmount;
                } else {
                    ShoppingBasket.__shakeShoppingBasket("You reached the maximum sold per customer.");
                    return false;
                }
            } else {
                ShoppingBasket.shoppingBasket.push({
                    "id": Math.floor(Math.random() * 1000) + 1,
                    "key": productKey,
                    "additionalKey": productOptionKey,
                    "additionalTitle": productOptionTitle,
                    "additionalPrice": productOptionPrice,
                    "title": product.data('product-title'),
                    "price": product.data('product-price'),
                    "amount": productAmount
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
                    var rowBlueprint = "<tr><td>%s %s</td><td><a href='#' class='decreaseBasketAmount' data-product-key='%s'><i class='fas fa-minus'></i></a><span class='px-4'>%s</span><a href='#' class='increaseBasketAmount' data-product-key='%s'><i class='fas fa-plus'></i></a></td><td>&euro; %s</td></tr>";

                    var additionalPrice = product.additionalPrice === undefined ? 0.0 : parseFloat(product.additionalPrice);
                    var price = parseFloat(product.price) + additionalPrice;
                    shoppingBasketTable += vsprintf(rowBlueprint, [
                        product.title,
                        product.additionalTitle === '' ? '' : 'with ' + product.additionalTitle,
                        product.id,
                        product.amount,
                        product.id,
                        parseFloat(Math.round(product.amount * price * 100) / 100).toFixed(2).replace(".", ",")
                    ]);

                    countItems += product.amount;

                    shoppingBasketTotal += product.amount * price;
                });

                $("#shoppingBasketTable").html(shoppingBasketTable);
                $("#shoppingBasketCount").html(countItems);
                $("#shoppingBasketTotal").html("&euro; " + parseFloat(Math.round(shoppingBasketTotal * 100) / 100).toFixed(2).replace(".", ","));

                $('.increaseBasketAmount').on('click', ShoppingBasket.__increaseProductAmount);
                $('.decreaseBasketAmount').on('click', ShoppingBasket.__decreaseProductAmount);
            }
        },

        __increaseProductAmount: function (e) {
            e.preventDefault();
            var indexProduct = ShoppingBasket.__indexIdContains(
                $(e.currentTarget).data('productKey'), ShoppingBasket.shoppingBasket);

            ShoppingBasket.shoppingBasket[indexProduct].amount++;

            Storages.localStorage.set(ShoppingBasket.LOCAL_STORAGE_SHOPPING_BASKET, ShoppingBasket.shoppingBasket);
            ShoppingBasket.__createShoppingBasketTable();
        },

        __decreaseProductAmount: function (e) {
            e.preventDefault();
            var indexProduct = ShoppingBasket.__indexIdContains(
                $(e.currentTarget).data('productKey'), ShoppingBasket.shoppingBasket);


            if (ShoppingBasket.shoppingBasket[indexProduct].amount > 1) {
                ShoppingBasket.shoppingBasket[indexProduct].amount--;
            } else {
                ShoppingBasket.shoppingBasket.splice(indexProduct, 1);
            }

            Storages.localStorage.set(ShoppingBasket.LOCAL_STORAGE_SHOPPING_BASKET, ShoppingBasket.shoppingBasket);
            ShoppingBasket.__createShoppingBasketTable();
        },

        __createEmptyShoppingBasketTable: function () {
            $("#shoppingBasketCount").html(0);
            $("#shoppingBasketTable").html("<tr><td colspan='3'>Shopping basket is empty!</td></tr>");
            $("#shoppingBasketTotal").html("");
        },

        __indexProductContains: function (key, additionalOptionKey, list) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].key === key && list[i].additionalKey === additionalOptionKey) {
                    return i;
                }
            }

            return -1;
        },

        __indexIdContains: function (id, list) {
            for (var i = 0; i < list.length; i++) {
                if (list[i].id === id) {
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

        __shakeShoppingBasket: function (message) {
            var shoppingBasketButton = $("#shoppingBasketButton");

            shoppingBasketButton.addClass('animated shake').one('webkitAnimationEnd mozAnimationEnd MSAnimationEnd oanimationend animationend', function () {
                $(this).removeClass('animated shake');
            });

            ShoppingBasket.__toast(message)
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