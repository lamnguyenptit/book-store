$(document).ready(function (){
    $(".linkMinus").on("click", function (evt){
        evt.preventDefault();
        decreaseQuantity($(this), true);
    });

    $(".linkMinusAnonymous").on("click", function (evt){
        evt.preventDefault();
        decreaseQuantity($(this), false);
    });

    $(".linkPlus").on("click", function (evt){
        evt.preventDefault();
        increaseQuantity($(this), true);
    });

    $(".linkPlusAnonymous").on("click", function (evt){
        evt.preventDefault();
        increaseQuantity($(this), false);
    });

    $(".removeProduct").on("click", function(evt){
        let productId = $(this).attr("pid");
        removeProductInCart(productId);
    });

    $(".removeProductAnonymous").on("click", function(evt){
        let productId = $(this).attr("pid");
        removeProductInCartAnonymous(productId);
    });
});

function showModalWarning(title, body){
    // $("#modalTitle").text(title);
    // $("#modalBody").text(body);
    // $("#modalDialog").modal();
    alert(body);
}

function decreaseQuantity(link, check){
    let productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;

    if(newQuantity > 0){
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity, check);
    } else {
        showModalWarning("Cảnh báo",'Số lượng sản phẩm không nhỏ hơn 1');
    }
}

function increaseQuantity(link, check){
    let productId = link.attr("pid");
    pieceAvailable = link.attr("maxQuan");
    // number = String.valueOf(pieceAvailable);
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;
    if(newQuantity > parseInt(pieceAvailable)) {
        showModalWarning("Cảnh báo", "Số lượng đặt vượt quá số lượng trong kho");
        newQuantity = 1;
    }
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity, check);
}

function updateQuantity(productId, quantity, check){
    quantity = $("#quantity" + productId).val();
    if(check == true){
        let url = contextPath + "cart/update/" + productId + "/" + quantity;
        $.post({
            url: url
        }).done(function (updatedSubtotal){
            // updateSubtotal(updatedSubtotal, productId);
            // updateTotal();
            window.location.href = "/cart";
        });
    }
    if(check == false){
        let url = contextPath + "cartAnonymous/update/" + productId + "/" + quantity;
        $.post({
            url: url
        }).done(function (updatedSubtotal){
            // updateSubtotal(updatedSubtotal, productId);
            // updateTotal();
            window.location.href = "/cartAnonymous";
        });
    }
}

// function updateQuantity(productId, quantity){
//     quantity = $("#quantity" + productId).val();
//     let url = contextPath + "cart/update/" + productId + "/" + quantity;
//
//     $.post({
//         url: url
//     }).done(function (updatedSubtotal){
//         updateSubtotal(updatedSubtotal, productId);
//         updateTotal();
//         // window.location.href = "/cart";
//     });
// }

function updateSubtotal(updatedSubtotal, productId){
    // alert(formatCurrentcy(parseInt(updatedSubtotal)))
    $("#orderSubTotal" + productId).text(formatCurrentcy(parseInt(updatedSubtotal))+'đ');
}

function updateTotal(){
    total = 0.0;
    productCount = 0;

    $(".subtotal").each(function (index, element){
        total += parseInt(element.innerHTML.match(/\d+\.?\d*/g).join(''));
    });
    $("#orderTotalMoney").text(formatCurrentcy(total)+ 'đ');
}

function removeProductInCart(productId){
    let url = contextPath + "cart/remove/" + productId;
    $.ajax({
        method: "POST",
        url: url,
        success: function (response){
            window.location.href = "/cart";
        }
    });
}

function removeProductInCartAnonymous(productId){
    let url = contextPath + "cartAnonymous/remove/" + productId;
    $.ajax({
        method: "POST",
        url: url,
        success: function (response){
            window.location.href = "/cartAnonymous";
        }
    });
}


$(function (){
    $(".btnContinueShop").click(function(){
        window.location.href = "/view";
    })
})

function showModalWarning(title, body){
    // $("#modalTitle").text(title);
    // $("#modalBody").text(body);
    // $("#modalDialog").modal();
    alert(body);
}

function formatCurrentcy(money){
    return money.toString().replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, " ");
}

