# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations
import django.contrib.gis.db.models.fields


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0016_rainfalldata'),
    ]

    operations = [
        migrations.CreateModel(
            name='soilsAndRainfall',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('soilID', models.CharField(default=None, max_length=150, null=True)),
                ('soilCode', models.CharField(max_length=25, null=True)),
                ('soilName', models.CharField(max_length=150, null=True)),
                ('soilLabel', models.CharField(max_length=150, null=True)),
                ('mapunit', models.CharField(max_length=25, null=True)),
                ('hectares', models.FloatField()),
                ('acres', models.FloatField()),
                ('secerosion', models.IntegerField()),
                ('terslope', models.IntegerField()),
                ('secslope', models.IntegerField()),
                ('secsoil', models.CharField(default=None, max_length=30)),
                ('capability', models.IntegerField()),
                ('water', models.CharField(default=None, max_length=25)),
                ('erosion', models.IntegerField()),
                ('domslope', models.IntegerField()),
                ('domsoil', models.IntegerField()),
                ('soil2001_i', models.IntegerField()),
                ('soil2001', models.IntegerField()),
                ('gavprimary', models.IntegerField()),
                ('gridcode', models.IntegerField()),
                ('geomData', django.contrib.gis.db.models.fields.GeometryField(srid=4326)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
