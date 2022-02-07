
$(document).ready(function (){
    $(".linkMinus").on("click", function (evt){
        evt.preventDefault();
        let productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) - 1;

        if(newQuantity > 0){
            quantityInput.val(newQuantity);
        } else {
            showModalWarning('Cảnh báo','Số lượng phải lớn hơn 0!');
        }
    });

    $(".linkPlus").on("click", function (evt){
        evt.preventDefault();
        let productId = $(this).attr("pid");
        maxQuantity = $(this).attr("maxQuan");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) + 1;
        if(maxQuantity >= newQuantity)
            quantityInput.val(newQuantity);
        else
            showModalWarning("Cảnh báo", "Số lượng đặt vượt quá số lượng trong kho");
    });
});

function showModalWarning(title, body){
    // $("#modalTitle").text(title);
    // $("#modalBody").text(body);
    // $("#modalDialog").modal();
    alert(body);
}


$(document).ready(function(){
    $("#buttonAdd2Cart").on("click", function(evt){
        quantity = $("#quantity" + productId).val();
        if( parseInt(quantity) == 0){
            evt.preventDefault();
            showModalWarning("Cảnh báo", "Số lượng phải lớn hơn 0!");
        }else{
            addToCart(quantity);
        }
    })
})

function addToCart(quantity){
    url =  contextPath + "cart/add/" + productId + "/" +quantity;
    $.ajax({
        method: "POST",
        url: url,
        data: {
            productId : productId,
            // _csrf: csrfValue
        },
        success: function (response){
            showModalWarning("Giỏ hàng", response);
        }

    })
}
