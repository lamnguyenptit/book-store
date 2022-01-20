$(document).ready(function (){
    $(".linkMinus").on("click", function (evt){
        evt.preventDefault();
        decreaseQuantity($(this));
    });

    $(".linkPlus").on("click", function (evt){
        evt.preventDefault();
        increaseQuantity($(this));
    });

    $(".removeProduct").on("click", function(evt){
        let productId = $(this).attr("pid");
        removeProductInCart(productId);
    });
});

function showModalWarning(title, body){
    // $("#modalTitle").text(title);
    // $("#modalBody").text(body);
    // $("#modalDialog").modal();
    alert(body);
}

function decreaseQuantity(link){
    let productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) - 1;

    if(newQuantity > 0){
        quantityInput.val(newQuantity);
        updateQuantity(productId, newQuantity);
    } else {
        showModalWarning("Cảnh báo",'Số lượng sản phẩm không nhỏ hơn 1');
    }
}

function increaseQuantity(link){
    let productId = link.attr("pid");
    quantityInput = $("#quantity" + productId);
    newQuantity = parseInt(quantityInput.val()) + 1;
    quantityInput.val(newQuantity);
    updateQuantity(productId, newQuantity);
}

function updateQuantity(productId, quantity){
    quantity = $("#quantity" + productId).val();
    let url = contextPath + "cart/update/" + productId + "/" + quantity;

    $.post({
        url: url
    }).done(function (updatedSubtotal){
        updateSubtotal(updatedSubtotal, productId);
        updateTotal();
        // window.location.href = "/cart";
    });
}

function updateSubtotal(updatedSubtotal, productId){
    $("#orderSubTotal" + productId).text(updatedSubtotal);
}

function updateTotal(){
    total = 0.0;
    productCount = 0;

    $(".subtotal").each(function (index, element){
        total += parseInt(element.innerHTML);
    });

    // if(productCount < 1){
    //     showEmptyShoppingCart();
    // } else {
    //     $("#total").text(formatCurrency(total));
    // }
    $("#orderTotalMoney").text(total);
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
$(function (){
    $(".btnContinueShop").click(function(){
        window.location.href = "/view";
    })
})