$(document).ready(function (){
    $(".linkMinus").on("click", function (evt){
        evt.preventDefault();
        let productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) - 1;

        if(newQuantity > 0){
            quantityInput.val(newQuantity);
        } else {
            alert('Số lượng phải lớn hơn 1!');
        }
    });

    $(".linkPlus").on("click", function (evt){
        evt.preventDefault();
        let productId = $(this).attr("pid");
        quantityInput = $("#quantity" + productId);
        newQuantity = parseInt(quantityInput.val()) + 1;
        quantityInput.val(newQuantity);
    });
});