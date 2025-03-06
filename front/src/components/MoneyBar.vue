<template>
    <div class="money-bar">
        <span>Argent: {{ money }}$</span>
    </div>
</template>

<script>
export default {
    name: 'MoneyBar',
    props: ['money']
};
</script>

<style scoped>
.money-bar {
    position: absolute;
    top: 10px;
    right: 10px;
    background-color: rgba(0, 0, 0, 0.7);
    color: white;
    padding: 10px;
    border-radius: 5px;
    border: 2px solid red;
    z-index: 10;
}
</style>