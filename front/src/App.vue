<template>
    <div id="app">
        <MainView />
    </div>
</template>

<script>
import MainView from './views/MainView.vue';

export default {
    name: 'App',
    components: {
        MainView
    }
};
</script>

<style>
/* Styles globaux */
body {
    margin: 0;
    padding: 0;
    font-family: Arial, sans-serif;
}
</style>