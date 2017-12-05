$(document).ready(function () {
    var storage = Storages.localStorage;
    var shoppingBasket = storage.get('tickets');

    createShoppingBasketTable(shoppingBasket);

    $("#emptyShoppingBasket").on('click', function () {
        storage.set('tickets', []);
        createShoppingBasketTable([]);
    });

    $("#orderForm").on('submit', function () {
        const productInput = '<input type="hidden" id="products%s.key" name="products[%s].key" value="%s">';

        var count = 0;
        $.each(storage.get('tickets'), function (index, ticket) {
            for (var i = 0; i < ticket.amount; i++) {
                $("#products").append(
                    vsprintf(productInput, [
                        count,
                        count,
                        ticket.key
                    ])
                );

                count++;
            }
        });

        storage.set('tickets', []);
    });

    $(".add-to-shopping-basket").on('click', function () {
        var shoppingBasket = storage.get('tickets');

        var indexProduct = indexProductContains($(this).data('product-key'), shoppingBasket);
        if (indexProduct >= 0) {
            shoppingBasket[indexProduct].amount++;
        } else {
            shoppingBasket.push({
                "key": $(this).data('product-key'),
                "title": $(this).data('product-title'),
                "cost": $(this).data('product-cost'),
                "amount": 1
            });
        }

        console.log(shoppingBasket);

        storage.set('tickets', shoppingBasket);
        createShoppingBasketTable(shoppingBasket);
    });

    function indexProductContains(key, list) {
        for (var i = 0; i < list.length; i++) {
            if (list[i].key === key) {
                return i;
            }
        }

        return -1;
    }

    function createShoppingBasketTable(shoppingBasket) {
        var shoppingBasketTable = "";
        var shoppingBasketTotal = 0;
        var countItems = 0;
        $.each(shoppingBasket, function (index, product) {
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
        } else if (shoppingBasket.length === 0) {
            shoppingBasketTable += "<tr><td colspan='3'>Shopping basket is empty!</td></tr>";
        }

        $("#shoppingBasketTable").html(shoppingBasketTable);
        $("#shoppingBasketCount").html(countItems);
        $("#shoppingBasketTotal").html("&euro; " + parseFloat(Math.round(shoppingBasketTotal * 100) / 100).toFixed(2).replace(".", ","));
    }
});