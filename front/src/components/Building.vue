<template>
    <img :src="building.imageUrl" :alt="building.type" class="building" :style="buildingStyle" />
</template>

<script>
export default {
    name: 'Building',
    props: ['building'],
    computed: {
        buildingStyle() {
            return {
                top: `${this.building.position.y}px`, // Position verticale
                left: `${this.building.position.x}px` // Position horizontale
            };
        }
    }
};
</script>

<style scoped>
.building {
    position: absolute;
    width: 400px;
    height: 400px;
    z-index: 10;
}
</style>