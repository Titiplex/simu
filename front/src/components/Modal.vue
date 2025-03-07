<template>
    <div class="window">
        <div class="window-header">
            <h3>{{ building.id }}</h3>
            <span class="close-button" v-on:click="close()">&#10005;</span>
        </div>
        <div class="window-content">
            <div class="building-info">
                <p>Capacité totale: {{ building.totalCapacity }}</p>
                <p>Occupation: {{ building.occupation }}</p>
                <p>Revenus par seconde: {{ building.earningPerSecond }}$</p>
                <p>Pertes par seconde: {{ building.lossPerSecond }}$</p>
            </div>
            <div class="services-info">
                <h3>Services</h3>
                <div v-for="service in building.services" :key="service.id" class="service">
                    <h4>{{ service.name }}</h4>
                    <div>
                        <p>Capacité: {{ service.occupation }} / {{ service.capacity }}</p>
                        <button> {{ Math.pow(service.level, 1.5)*100 }} </button>
                    </div>
                    <p></p>
                    <!-- <p>Occupation: {{ service.occupation }}</p> -->
                </div>
            </div>
        </div>
    </div>
</template>

<script>
export default {
    name: 'Modal',
    props: {
        building: {
            type: Object,
            required: true
        }
    },
    methods: {
        close() {
            this.$emit('close');
        }
    }
};
</script>

<style scoped>
.window {
    position: fixed;
    bottom: 0;
    right: 0;
    width: 300px;
    height: 400px;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 8px 8px 0 0;
    box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
    overflow: hidden;
    z-index: 100;
    left: 0;
    transition: ease-in-out 0.7s;
}
.window.translate {
    transform: translateY(-50vh);
}

.window .window-header {
    background-color: #f1f1f1;
    padding-left: 15px;
    padding-right: 15px;
    border-bottom: 1px solid #ddd;
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.window-content {
    padding: 20px;
    height: calc(100% - 50px); /* Adjust height to account for header */
    overflow-y: auto;
}

.close-button {
    font-size: 20px;
    cursor: pointer;
    color: #ff4444;
}

.close-button:hover {
    color: #cc0000;
}

.building-info, .services-info {
    margin: 15px 0;
}

.service {
    border: 1px solid #ddd;
    padding: 10px;
    margin: 10px 0;
    border-radius: 4px;
}
</style>