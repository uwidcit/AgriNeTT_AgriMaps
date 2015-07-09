# -*- coding: utf-8 -*-
from __future__ import unicode_literals

from django.db import models, migrations


class Migration(migrations.Migration):

    dependencies = [
        ('agriApp', '0009_auto_20150203_1314'),
    ]

    operations = [
        migrations.CreateModel(
            name='soilFeatures',
            fields=[
                ('id', models.AutoField(verbose_name='ID', serialize=False, auto_created=True, primary_key=True)),
                ('domsoil', models.CharField(max_length=25)),
                ('sand_prop', models.CharField(max_length=30)),
                ('silt_prop', models.CharField(max_length=30)),
                ('clay_prop', models.CharField(max_length=30)),
                ('horizon_to_dept', models.FloatField()),
                ('phhorizon1', models.CharField(max_length=30)),
                ('ca', models.CharField(max_length=30)),
                ('mg', models.CharField(max_length=30)),
                ('k', models.CharField(max_length=30)),
                ('na', models.CharField(max_length=30)),
                ('cec', models.CharField(max_length=30)),
                ('exch_cations', models.CharField(max_length=30)),
                ('c', models.CharField(max_length=30)),
                ('n', models.CharField(max_length=30)),
                ('freecaco3', models.CharField(max_length=30)),
                ('fertility', models.CharField(max_length=30)),
            ],
            options={
            },
            bases=(models.Model,),
        ),
    ]
