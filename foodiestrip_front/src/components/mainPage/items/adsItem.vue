<script setup>

</script>

<template>
    <div id="demo" class="carousel slide col-md-12 mt-3" data-bs-ride="carousel">
        <!-- Indicators/dots -->
        <div class="carousel-indicators">
            <button type="button" data-bs-target="#demo" data-bs-slide-to="0" class="active"></button>
            <button type="button" data-bs-target="#demo" data-bs-slide-to="1"></button>
            <button type="button" data-bs-target="#demo" data-bs-slide-to="2"></button>
            <button type="button" data-bs-target="#demo" data-bs-slide-to="3"></button>
        </div>

        <!-- The slideshow/carousel -->
        <div class="carousel-inner">
            <div class="carousel-item active">
                <img src="/images/mainViewImg/test.png" alt="Ads1" class="d-block w-100" />
            </div>
            <div class="carousel-item">
                <img src="/images/mainViewImg/adimg1.jpg" alt="Ads1" class="d-block w-100" />
            </div>
            <div class="carousel-item">
                <img src="/images/mainViewImg/adimg2.jpg" alt="Ads2" class="d-block w-100" />
            </div>
        </div>

        <!-- Left and right controls/icons -->
        <button class="carousel-control-prev" type="button" data-bs-target="#demo" data-bs-slide="prev">
            <span class="carousel-control-prev-icon"></span>
        </button>
        <button class="carousel-control-next" type="button" data-bs-target="#demo" data-bs-slide="next">
            <span class="carousel-control-next-icon"></span>
        </button>
    </div>
</template>

<style scoped>
.carousel-control-prev:hover{
    background-color: transparent;
}
.carousel-control-next:hover{
    background-color: transparent;
}
</style>