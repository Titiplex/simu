<template>
    <div class="window">
        <div class="window-header">
            <h3>{{ building.id }}</h3>
            <span class="close-button" v-on:click="close()">&#10005;</span>
        </div>
        <div class="window-content">
            <div class="building-info">
                <p><i class="fas fa-hospital"></i> Capacité totale: {{ building.occupation }} / {{ building.capacity }}</p>
                <p><i class="fas fa-dollar-sign"></i> Revenus par seconde: {{ building.earningPerSecond }}$</p>
                <p><i class="fas fa-money-bill-wave"></i> Pertes par seconde: {{ building.lossPerSecond }}$</p>
            </div>
            <div class="services-info">
                <h3>Services</h3>
                <div v-for="service in building.services" :key="service.id" class="service">
                    <h4>{{ service.name }}</h4>
                    <div class="service-attribut">
                        <p>Capacité: {{ service.occupation }} / {{ service.capacity }}</p>
                        <button v-on:click="buyCapLvl(service)" > {{ Math.floor(Math.pow(service.capacity, 2)/2) }} </button>
                    </div>
                    <div class="service-attribut">
                        <p>Qualité de service: {{ service.level }}</p>
                        <button v-on:click="buyServiceLvl(service)"> {{ Math.floor(Math.pow(service.level, 4)*100 )}} </button>
                    </div>
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
        },
        buyServiceLvl(service) {
            const cost = Math.floor(Math.pow(service.level, 4) * 100);
            if (this.$parent.money >= cost) {
                service.level += 0.05;
                service.level = Math.floor(service.level*100)/100;
                this.$parent.money -= cost;
            } else {
                alert("Pas assez d'argent !");
            }
        },
        buyCapLvl (service) {
            const cost = Math.floor(Math.pow(service.capacity, 2)/2);
            if (this.$parent.money >= cost) {
                service.capacity += 1;
                this.$parent.money -= cost;
            } else {
                alert("Pas assez d'argent !");
            }
        }
    }
};
</script>

<style scoped>
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css');

.window {
    position: fixed;
    bottom: 0;
    right: 0;
    width: 300px;
    height: 400px;
    background-color: white;
    border: 1px solid #ddd;
    border-radius: 15px 15px 0 0;
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

.building-info p {
    display: flex;
    align-items: center;
}

.building-info i {
    margin-right: 8px;
    width: 20px;
    text-align: center;
}

.service {
    border: 1px solid #ddd;
    padding: 10px;
    margin: 10px 0;
    border-radius: 4px;
}
.service-attribut {
    display: flex;
    justify-content: space-between;
}
.service button {
    background-color: #4CAF50;
    color: white;
    padding: 0;
    border: none;
    border-radius: 4px;
    padding: 0px 10px;
    cursor: pointer;
}
</style>