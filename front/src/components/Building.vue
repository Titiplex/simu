<template>
    <div>
        <button v-on:click="$emit('open', building)">
            <img :src="building.imageUrl" :alt="building.id" class="building" :style="buildingStyle" />
        </button>
    </div>
</template>

<script>
export default {
    name: 'Building',
    props: {
        building: {
            type: Object,
            required: true
        }
    },
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