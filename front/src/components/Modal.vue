<template>
    <div class="window">
        <div class="window-content">
            <button class="close-button" v-on:click="close()">Fermer</button>
            <h2>{{ building.type }}</h2>
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
                    <p>Capacité: {{ service.capacity }}</p>
                    <p>Occupation: {{ service.occupation }}</p>
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
    margin-bottom: -50vh;
    transition: ease-in-out 0.7s;
    /* transform: translateY(-50vh); */
}
.window.translate {
    transform: translateY(-50vh);
}
.window-content {
    padding: 20px;
    height: 100%;
    overflow-y: auto;
}

.close-button {
    background-color: #ff4444;
    color: white;
    border: none;
    padding: 8px 16px;
    border-radius: 4px;
    cursor: pointer;
    margin-bottom: 10px;
}

.close-button:hover {
    background-color: #cc0000;
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