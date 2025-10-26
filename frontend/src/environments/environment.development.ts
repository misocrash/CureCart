const host = 'http://curecart-ALB-621411160.us-east-1.elb.amazonaws.com'; // keeps changing 

export const environment = {
    production: false,
    endpoints: {
        host: `${host}`,
        fetchAllOrdersAdmin: `${host}/api/admin/orders`,
        userBaseEndpoint: `${host}/api/users`,
        medicineBaseEndpoint: `${host}/api/medicines`
    }
};
